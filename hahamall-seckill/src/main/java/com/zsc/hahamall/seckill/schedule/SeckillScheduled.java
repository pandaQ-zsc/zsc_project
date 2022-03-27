package com.zsc.hahamall.seckill.schedule;

/**
 * Created by XQ
 * 2022/3/24
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 秒杀商品的定时上架
 * 每天晚上3点，上架最近需要秒杀的商品
 */
@Service
@Slf4j
public class SeckillScheduled {
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){

    }

}
