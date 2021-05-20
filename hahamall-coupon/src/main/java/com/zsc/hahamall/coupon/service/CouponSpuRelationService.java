package com.zsc.hahamall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 22:45:34
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

