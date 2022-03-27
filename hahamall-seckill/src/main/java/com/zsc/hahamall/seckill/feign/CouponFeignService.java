package com.zsc.hahamall.seckill.feign;

import com.zsc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by XQ
 * 2022/3/24
 */
@FeignClient("hahamall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/latest3DaySession")
     R getLatest3DaySession();
}

