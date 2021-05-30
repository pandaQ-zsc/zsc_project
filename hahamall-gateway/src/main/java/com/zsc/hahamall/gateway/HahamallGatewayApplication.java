package com.zsc.hahamall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//(exclude = {DataSourceAutoConfiguration.class}) -- 排除由于common引入的mybatis模块需要配置数据源的冲突
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HahamallGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HahamallGatewayApplication.class, args);
	}

}
