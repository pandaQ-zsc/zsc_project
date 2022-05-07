package com.zsc.hahamall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>Title: ThreadPoolConfigProperties</p>
 * Description：
 * date：2021/6/25 11:04
 */
@ConfigurationProperties(prefix = "hahamall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

	private Integer coreSize;

	private Integer maxSize;

	private Integer keepAliveTime;
}
