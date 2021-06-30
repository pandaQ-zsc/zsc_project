package com.zsc.hahamall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 开启feign客户端的远程调用别的服务的功能
 * 1.引入open-feign  --  具有远程调用别人的能力
 * 2.编写一个接口(feign包下)，告诉SpringCloud这个接口需要调用远程服务
 *  2.1 在接口里声明@FeignClient("hahamall-coupon")他是一个远程调用客户端且要调用coupon服务
 *  2.2 要调用coupon服务的/coupon/coupon/member/list方法
 *  3 开启远程调用功能 @EnableFeignClients，要指定远程调用功能放的基础包
 */
//注解开启服务注册与发现功能
//在member的配置类上加注解@EnableDiscoveryClient，告诉member是一个远程调用客户端，member要调用东西的
@EnableFeignClients(basePackages = "com.zsc.hahamall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class HahamallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(HahamallMemberApplication.class, args);
	}

}
