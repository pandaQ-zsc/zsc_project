package com.zsc.hahamall.coupon.dao;

import com.zsc.hahamall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 22:45:34
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
