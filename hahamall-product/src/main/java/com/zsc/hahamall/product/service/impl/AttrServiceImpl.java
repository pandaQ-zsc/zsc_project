package com.zsc.hahamall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zsc.common.cosntant.ProductConstant;
import com.zsc.hahamall.product.dao.AttrAttrgroupRelationDao;
import com.zsc.hahamall.product.dao.AttrGroupDao;
import com.zsc.hahamall.product.dao.CategoryDao;
import com.zsc.hahamall.product.entity.AttrAttrgroupRelationEntity;
import com.zsc.hahamall.product.entity.AttrGroupEntity;
import com.zsc.hahamall.product.entity.CategoryEntity;
import com.zsc.hahamall.product.service.CategoryService;
import com.zsc.hahamall.product.service.ProductAttrValueService;
import com.zsc.hahamall.product.vo.AttrGroupRelationVo;
import com.zsc.hahamall.product.vo.AttrRespVo;
import com.zsc.hahamall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsc.common.utils.PageUtils;
import com.zsc.common.utils.Query;

import com.zsc.hahamall.product.dao.AttrDao;
import com.zsc.hahamall.product.entity.AttrEntity;
import com.zsc.hahamall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据分组id查找关联的所有基本属性
     * @param attrgroupId
     * @return
     * /product/attrgroup/{attrgroupId}/attr/relation   前段已知参数是：attrgroupId
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        System.out.println("AttrAttrgroupRelationEntity========"+entities);
        List<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        if (attrIds == null || attrIds.size() == 0){
            return null ;
        }
        //this.   是 attrService.
        List<AttrEntity> attrEntities = this.listByIds(attrIds);
        System.out.println("attrEntites=================="+attrEntities);
        return attrEntities;
    }

    // vos : [{"attrId":1,"attrGroupId":2}]
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //这样写需要一个一删除
        //relationDao.delete(new QueryWrapper<>().eq("attrId",1L).eq("attr_group_id",1L));
        //-将数组（Arrays）转化为list列表   每个item就是attrGroupRelationVo
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            //将请求参数的AttrGroupRelationVo的每个属性值对应复制到relationEntity
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
            //把返回的relationEntity收集成一个集合
        }).collect(Collectors.toList());
        //改进后：批量删除
        relationDao.deleteBatchRelation(entities);
    }


    /**
     * 当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1、当前分组只能关联自己所属的分类里面的所有属性  attrGroupEntity--当前分组信息
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2、当前分组只能关联别的分组没有引用的属性
        //2.1)、当前分类下的其他分组
        //找到pms_attr_group中的同组（cattelog_id为255）的所有3个体
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //  列表集合的查询筛选需要用strem()的方法。返回筛选后新的List集合   1,2,5
        List<Long> collect = group.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //2.2)、这些分组关联的属性  pms_attr_attrgroup_relation中的 5 4 2对应的AttrAttrgroupRelationEntity
        List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));

        //获取其他分组已经关联的所有属性
            //attrIds : 4  也就是pms_attr_attrgroup_relation中的 attr_id
        List<Long> attrIds = groupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        //2.3)、从当前分类的所有属性中移除这些属性
        //       this.baseMapper -拿到attrDao    --AttrEntities是当前分组（catelog_id:255）可以关联的【所有属性】
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null && attrIds.size()>0){
            wrapper.notIn("attr_id", attrIds);
        }
        //返回分页对象
        String key = (String) params.get("key");
        //wrapper就是AttrEntity 的个体
        if (!StringUtils.isEmpty(key)){
            wrapper.and((w)->{w.eq("attr_id",key).or().like("attr_name",key);});
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
    return baseMapper.selectSearchAttrIds(attrIds);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
//        attrEntity.setAttrName(attr.getAttrName());
        //将页面vo来源的值 封装到po数据库里面
        BeanUtils.copyProperties(attr, attrEntity);
        //1、保存基本数据
        this.save(attrEntity);
        //2、保存关联关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId()!=null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        System.out.println( ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
//        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type) ? 1:0);
        //查询条件
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        ;
        //检索条件
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //检索值: attr_id attr_name
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        //这里的page 只能包含AttrEntity中的属性
        IPage<AttrEntity> page = this.page(
                //getPage(params)将分页条件分装成Ipage参数
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            //  vo中含有 entity中的基本属性
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //1、设置分类和分组的名字
            //1)通过属性id 查询一条relation关系记录实体
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().
                        eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null&& attrId.getAttrGroupId()!=null) {
                    //通过relation实体的groupId得到分组(attrGroupDao、pms_attr_group)的实体类
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    //将查到的分组name放入到AttrRespVo类。
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            //通过attrEntity查询分组id，得到分类实体(categoryEntity)
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                //将分类实体的(categoryName)放入到AttrRespVo类中
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;

    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //设置分组信息 "attrGroupId": 1, //分组id
            AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrgroupRelation != null) {
                respVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        //设置分类信息，分类id  "catelogId": 225
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        //分类完整路径 "catelogPath": [2, 34, 225]
        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //修改关联分组
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));
            if (count > 0) {

                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }


}