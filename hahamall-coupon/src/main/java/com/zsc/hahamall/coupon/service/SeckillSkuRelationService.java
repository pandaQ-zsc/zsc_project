package com.zsc.hahamall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 22:45:34
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

