package com.zsc.hahamall.seckill.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 异步加对定时任务来解决定时任务不堵塞的功能
 * Created by XQ
 * 2022/3/24
 */
//@EnableScheduling
//@EnableAsync
@Component
@Slf4j
public class HelloSchedule {
    @Async
    @Scheduled(cron = "* * * * * ?")
    public void hello(){
        log.info("hello....");
    }
}
