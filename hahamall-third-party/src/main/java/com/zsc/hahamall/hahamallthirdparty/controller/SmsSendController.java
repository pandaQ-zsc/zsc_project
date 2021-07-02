package com.zsc.hahamall.hahamallthirdparty.controller;

import com.zsc.common.utils.R;
import com.zsc.hahamall.hahamallthirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    SmsComponent smsComponent;

    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        smsComponent.senSmsCode(phone);
        return R.ok();
    }
}
