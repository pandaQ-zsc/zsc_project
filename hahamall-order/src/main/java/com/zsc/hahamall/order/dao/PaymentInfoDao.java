package com.zsc.hahamall.order.dao;

import com.zsc.hahamall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-20 11:03:12
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
