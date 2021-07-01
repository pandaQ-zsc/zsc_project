package com.zsc.hahamall.member.service.impl;

import com.zsc.hahamall.member.exception.PhoneExistException;
import com.zsc.hahamall.member.exception.UserNameExistException;
import com.zsc.hahamall.member.vo.MemberLoginVo;
import com.zsc.hahamall.member.vo.SocialUser;
import com.zsc.hahamall.member.vo.UserRegisterVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsc.common.utils.PageUtils;
import com.zsc.common.utils.Query;

import com.zsc.hahamall.member.dao.MemberDao;
import com.zsc.hahamall.member.entity.MemberEntity;
import com.zsc.hahamall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVo userRegisterVo) throws PhoneExistException, UserNameExistException {

    }

    @Override
    public void checkPhone(String phone) throws PhoneExistException {

    }

    @Override
    public void checkUserName(String username) throws UserNameExistException {

    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        return null;
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        return null;
    }

}