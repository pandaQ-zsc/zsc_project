package com.zsc.hahamall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zsc.common.to.mq.SecKillOrderTo;
import com.zsc.common.utils.R;
import com.zsc.common.vo.MemberRsepVo;
import com.zsc.hahamall.seckill.feign.CouponFeignService;
import com.zsc.hahamall.seckill.feign.ProductFeignService;
import com.zsc.hahamall.seckill.interceptor.LoginUserInterceptor;
import com.zsc.hahamall.seckill.service.SeckillService;
import com.zsc.hahamall.seckill.to.SeckillSkuRedisTo;
import com.zsc.hahamall.seckill.vo.SeckillSessionsWithSkus;
import com.zsc.hahamall.seckill.vo.SkuInfoVo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; // + 商品随机码

    //上架最近三天参数秒杀活动商品信息
    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1.扫描最近3天需要参与秒杀的活动
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

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRsepVo memberRsepVo = LoginUserInterceptor.threadLocal.get();
        // 1.获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if(StringUtils.isEmpty(json)){
            return null;
        }else{
            SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            // 校验合法性
            long time = new Date().getTime();
            if(time >= redisTo.getStartTime() && time <= redisTo.getEndTime()){
                // 1.校验随机码跟商品id是否匹配
                String randomCode = redisTo.getRandomCode();
                String skuId = redisTo.getPromotionSessionId() + "-" + redisTo.getSkuId();

                if(randomCode.equals(key) && killId.equals(skuId)){
                    // 2.说明数据合法
                    BigDecimal limit = redisTo.getSeckillLimit();
                    if(num <= limit.intValue()){
                        // 3.验证这个人是否已经购买过了
                        String redisKey = memberRsepVo.getId() + "-" + skuId;
                        // 让数据自动过期
                        long ttl = redisTo.getEndTime() - redisTo.getStartTime();

                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl<0?0:ttl, TimeUnit.MILLISECONDS);
                        if(aBoolean){
                            // 占位成功 说明从来没买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            boolean acquire = semaphore.tryAcquire(num);
                            if(acquire){
                                // 秒杀成功
                                // 快速下单 发送MQ
//                                String orderSn = IdWorker.getTimeId() + UUID.randomUUID().toString().replace("-","").substring(7,8);
                                String orderSn = new Date()+ UUID.randomUUID().toString().replace("-","").substring(7,8);
                                SecKillOrderTo orderTo = new SecKillOrderTo();
                                orderTo.setOrderSn(orderSn);
                                orderTo.setMemberId(memberRsepVo.getId());
                                orderTo.setNum(num);
                                orderTo.setSkuId(redisTo.getSkuId());
                                orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order", orderTo);
                                return orderSn;
                            }
                        }else {
                            return null;
                        }
                    }
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
        return null;
    }

    //根据活动场次，将sessionInfo信息进行缓存。
    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
//            Boolean hasKey = redisTemplate.hasKey(key);
//            if (!hasKey) {
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getSkuId().toString()).collect(Collectors.toList());
            //缓存活动信息  添加key对应的list
                redisTemplate.opsForList().leftPushAll(key, collect);
//            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            //准备hash操作
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                        //缓存商品（以json格式为value）
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
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
                        ops.put(seckillSkuVo.getId().toString() + "-" + seckillSkuVo.getSkuId().toString(), jsonString);
                        //如果当前这个场次的商品库存已经上架就不需要再上架
                        //5.使用库存作为分布式信号量 ， 限流
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                    }
            );
        });
    }
 }
