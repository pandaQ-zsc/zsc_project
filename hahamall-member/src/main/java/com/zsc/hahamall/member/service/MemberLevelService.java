package com.zsc.hahamall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-20 10:00:17
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

