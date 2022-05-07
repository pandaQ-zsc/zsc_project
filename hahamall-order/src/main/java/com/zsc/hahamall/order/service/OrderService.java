package com.zsc.hahamall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.to.mq.SecKillOrderTo;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.order.entity.OrderEntity;
import com.zsc.hahamall.order.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-20 11:03:12
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 给订单确认页返回需要的数据
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单操作
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

    /**
     * 获取当前订单的支付信息
     */
    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(@Param("params") Map<String, Object> params);

    /**
     * 处理支付宝的返回数据
     */
    String handlePayResult(PayAsyncVo vo);

    void createSecKillOrder(SecKillOrderTo secKillOrderTo);
}

