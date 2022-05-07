package com.zsc.hahamall.order.listener;


import com.rabbitmq.client.Channel;
import com.zsc.common.to.mq.SecKillOrderTo;
import com.zsc.hahamall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>Title: OrderSecKillListener</p>
 * Description：
 * date：2021/7/9 20:23
 */
//@RabbitListener(queues = "order.seckill.order.queue")
//@Component
public class OrderSecKillListener {

	@Autowired
	private OrderService orderService;

	@RabbitHandler
	public void listener(SecKillOrderTo secKillOrderTo, Channel channel, Message message) throws IOException {
		try {
			// 创建秒杀单的信息
			orderService.createSecKillOrder(secKillOrderTo);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
}
