package com.zsc.hahamall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.common.utils.PageUtils;
import com.zsc.hahamall.member.entity.MemberEntity;
import com.zsc.hahamall.member.exception.PhoneExistException;
import com.zsc.hahamall.member.exception.UserNameExistException;
import com.zsc.hahamall.member.vo.MemberLoginVo;
import com.zsc.hahamall.member.vo.SocialUser;
import com.zsc.hahamall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-20 10:00:17
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo userRegisterVo) throws PhoneExistException, UserNameExistException;

    void checkPhone(String phone) throws PhoneExistException;

    void checkUserName(String username) throws UserNameExistException;

    /**
     * 普通登录
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交登录
     */
    MemberEntity login(SocialUser socialUser);
}

