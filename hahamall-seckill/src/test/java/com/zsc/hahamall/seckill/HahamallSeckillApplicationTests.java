package com.zsc.hahamall.seckill;

import com.zsc.common.utils.R;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//@SpringBootTest
class HahamallSeckillApplicationTests {
    @Test
    void contextLoads() {

        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        String ss = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm:ss"));
        System.out.println(ss);
        System.out.println("=======================");
        R r = new R();
        r.put("index","666");
        r.put("didi","8888");

        System.out.println(r.toString());
    }

}
