package com.zsc.hahamall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.zsc.hahamall.ware.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class HahamallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(HahamallWareApplication.class, args);
	}

}
