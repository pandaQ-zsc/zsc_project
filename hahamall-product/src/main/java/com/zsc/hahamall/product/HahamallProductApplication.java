package com.zsc.hahamall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.zsc.hahamall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.zsc.hahamall.product.dao")
@SpringBootApplication
public class HahamallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(HahamallProductApplication.class, args);
	}

}
