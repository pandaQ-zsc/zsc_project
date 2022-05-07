package com.zsc.hahamall.seckill.schedule;

/**
 * Created by XQ
 * 2022/3/24
 */

import com.zsc.hahamall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品的定时上架
 * 每天晚上3点，上架最近需要秒杀的商品
 */
@Service
@Slf4j
public class SeckillScheduled {
    @Autowired
    SeckillService seckillService;
    @Autowired
    RedissonClient redissonClient;

    private final  String upload_lock = "seckill:upload:lock";
    //幂等性处理
    @Scheduled(cron = "*/3 * * * * ?")
    public void uploadSeckillSkuLatest3Days(){
        log.info("上架秒杀的商品信息");
        //分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try {
        //上架最近三天参数秒杀活动商品信息
        seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }


    }

}
