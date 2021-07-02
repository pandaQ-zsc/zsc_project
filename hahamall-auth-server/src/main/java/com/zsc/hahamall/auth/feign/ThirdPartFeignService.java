package com.zsc.hahamall.auth.feign;

import com.zsc.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("hahamall-third-party")
public interface ThirdPartFeignService {


	@GetMapping("/sms/sendcode")
	public R sendCode(@RequestParam("phone") String phone);

}
