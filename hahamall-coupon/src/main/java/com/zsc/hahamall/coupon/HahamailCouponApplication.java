package com.zsc.hahamall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.zsc.hahamall.coupon.dao")
@SpringBootApplication
public class HahamailCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(HahamailCouponApplication.class, args);
	}

}
