package com.zsc.hahamall.product.dao;

import com.zsc.hahamall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 17:03:41
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
