package com.zsc.hahamall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 22:45:34
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

