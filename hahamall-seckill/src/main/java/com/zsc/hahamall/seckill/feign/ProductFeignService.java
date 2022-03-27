package com.zsc.hahamall.seckill.feign;

import com.zsc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>Title: ProductFeignService</p>
 * Description：
 * date：2021/7/6 19:16
 */
@FeignClient("hahamall-product")
public interface ProductFeignService {
	@RequestMapping("/product/skuinfo/info/{skuId}")
	R getSkuInfo(@PathVariable("skuId") Long skuId);
}
