package com.zsc.hahamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 17:03:41
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

