package com.zsc.hahamall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zsc.common.utils.R;
import com.zsc.hahamall.seckill.feign.CouponFeignService;
import com.zsc.hahamall.seckill.feign.ProductFeignService;
import com.zsc.hahamall.seckill.service.SeckillService;
import com.zsc.hahamall.seckill.to.SecKillSkuRedisTo;
import com.zsc.hahamall.seckill.vo.SeckillSessionsWithSkus;
import com.zsc.hahamall.seckill.vo.SeckillSkuVo;
import com.zsc.hahamall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by XQ
 * 2022/3/24
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock"; // + 商品随机码

    //上架最近三天参数秒杀活动商品信息
    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1.扫描需要参与秒杀的活动
        R session = couponFeignService.getLatest3DaySession();
        if (session.getCode() == 0) {
            //上架商品
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            //缓存到redis
            //1、缓存活动信息
            saveSessionInfos(sessionData);
            //2、缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);
        }
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            List<String> collect = session.getRelationSkus().stream().map(item -> item.getId().toString()).collect(Collectors.toList());
            //缓存活动信息  添加key对应的list
            redisTemplate.opsForList().leftPushAll(key, collect);
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            //准备hash操作
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //缓存商品（以json格式为value）
                SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                //1、sku的基本数据
                R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                if (skuInfo.getCode() == 0) {
                    SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    redisTo.setSkuInfoVo(info);
                }
                //2、sku的秒杀信息
                BeanUtils.copyProperties(seckillSkuVo, redisTo);
                //3、设置上当前商品的秒杀时间信息
                redisTo.setStartTime(session.getStartTime().getTime());
                redisTo.setEndTime(session.getEndTime().getTime());
                //4、设置当前商品的秒杀信息(设置随机码 seckill?skuId=1&key=sdjashkjdshak)
                String token = UUID.randomUUID().toString().replace("-", "");

                redisTo.setRandomCode(token);

                String jsonString = JSON.toJSONString(redisTo);
                ops.put(seckillSkuVo.getId(), jsonString);
                //如果当前这个场次的商品库存已经上架就不需要再上架
                //5.使用库存作为分布式信号量 ， 限流
                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());

            });
        });
    }
}
