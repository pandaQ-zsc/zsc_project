package com.zsc.hahamall.member.service.impl;

import com.zsc.hahamall.member.dao.MemberLevelDao;
import com.zsc.hahamall.member.entity.MemberLevelEntity;
import com.zsc.hahamall.member.exception.PhoneExistException;
import com.zsc.hahamall.member.exception.UserNameExistException;
import com.zsc.hahamall.member.vo.MemberLoginVo;
import com.zsc.hahamall.member.vo.SocialUser;
import com.zsc.hahamall.member.vo.UserRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsc.common.utils.PageUtils;
import com.zsc.common.utils.Query;

import com.zsc.hahamall.member.dao.MemberDao;
import com.zsc.hahamall.member.entity.MemberEntity;
import com.zsc.hahamall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;
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

        MemberEntity entity = new MemberEntity();
        // 设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(memberLevelEntity.getId());

        // 检查手机号 用户名是否唯一
        checkPhone(userRegisterVo.getPhone());
        checkUserName(userRegisterVo.getUserName());

        entity.setMobile(userRegisterVo.getPhone());
        entity.setUsername(userRegisterVo.getUserName());

        // 密码要加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(userRegisterVo.getPassword()));
        // 其他的默认信息
        entity.setCity("江西 南昌");
        entity.setCreateTime(new Date());
        entity.setStatus(0);
        entity.setNickname(userRegisterVo.getUserName());
        entity.setBirth(new Date());
        entity.setEmail("xxx@qq.com");
        entity.setGender(1);
        entity.setJob("JAVA");
        baseMapper.insert(entity);
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