package com.zsc.hahamall.coupon.dao;

import com.zsc.hahamall.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 22:45:34
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
