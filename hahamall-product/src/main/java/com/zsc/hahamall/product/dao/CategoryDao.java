package com.zsc.hahamall.product.dao;

import com.zsc.hahamall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 17:03:41
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
