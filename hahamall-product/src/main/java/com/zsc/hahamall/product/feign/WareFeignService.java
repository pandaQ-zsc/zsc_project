package com.zsc.hahamall.product.feign;

import com.zsc.common.to.SkuHasStockVo;
import com.zsc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("hahamall-ware")
public interface WareFeignService {
    /**
     * 1、R设计的时候可以加上泛型
     * 2、直接返回想要的数据
     * 3、自己封装解析结果
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
     R getSkusHasStock(@RequestBody List<Long> skuIds);
}
