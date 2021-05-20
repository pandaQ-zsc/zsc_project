package com.zsc.hahamall.order.dao;

import com.zsc.hahamall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-20 11:03:12
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
