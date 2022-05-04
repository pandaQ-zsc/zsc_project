package com.zsc.hahamall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zsc.hahamall.product.service.CategoryBrandRelationService;
import com.zsc.hahamall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsc.common.utils.PageUtils;
import com.zsc.common.utils.Query;

import com.zsc.hahamall.product.dao.CategoryDao;
import com.zsc.hahamall.product.entity.CategoryEntity;
import com.zsc.hahamall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    //根据public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {
    //    @Autowired
    //    protected M baseMapper;
    //---------------可以得知该类中的baseMapper就是Category
//    @Autowired
//    CategoryDao categoryDao;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2、组装成父子的树形结构

        //2.1)、组装所有的一级分类   .collect(..)  表示Stream类将这个集合收集成toList();
        //stream()方法可以将数据先转化成流数据 这样有利于性能提高
        // categoryEntity.getParentCid() == 0  查询一级目录
        List<CategoryEntity> level1Menus = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    //将菜单更改,再返回
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // 1、检查当前删除的菜单，是否被别的地方引用

        //这个删除如果不配逻辑删除就是是直接删除 相当于物理删除 会造成数据库中的数据删除
        //配了逻辑删除  logic-delete-value后则可以自定义状态
        baseMapper.deleteBatchIds(asList);
    }


    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }
    @Cacheable("category")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;

    }


    //redis中查询
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {

        /**
         * 1、空结果缓存+设置过期时间： 解决缓存穿透
         * 2、设置过期时间（加随机值）：解决缓存雪崩
         * 3、加锁：解决缓存击穿
         */

        //1、加入缓存逻辑，缓存中存放的数据是json字符串
        // 拿出来的是json字符串 还需要逆转成能用的对象类数据【序列化与反序列化】
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            //2、缓存中没有，查询数据库
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
//            //3、查到的数据再放入缓存,将对象转为json放入缓存中,此时s是JSON格式
//            String s = JSON.toJSONString(catalogJsonFromDb);
//            redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
            return catalogJsonFromDb;
        }
        // 走到这里说明缓存中有数据 直接拿出Json数据转化成响应的对象
        // 拿出来的是json字符串 还需要逆转成能用的对象类数据【序列化与反序列化】  、TypeReference是protected类型因此需要使用匿名内部类的形式创建出来传入。
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
       
        return result;
    }


    //从数据库中查询并封装三级分类的数据
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
        //只要是同一把锁，就能锁住需要这个锁的所有线程  --本地锁-进程锁
        //1、synchronized (this) ：this代表当前实例对象  SpringBoot所有的组件在容器中都是单例的  -- 只适合一台服务器
        // synchronized (this)在n台服务器访问时候一样会产生n个线程同时进来查询数据库相同的数据 --本地锁，只能锁住当前进程
        synchronized (this) {
            //得到锁以后，应该再去缓存中确定一次，如果没有才继续查询
            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
            if (!StringUtils.isEmpty(catalogJSON)){
                Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
                return  result;
            }
            /**
             * 1、将数据库的多次查询变为一次
             */
            List<CategoryEntity> selectList = baseMapper.selectList(null);

            //1、查出所有1级分类
            List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);
            //2、封装数据
            Map<String, List<Catalog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                //1、每一个的一级分类,查到这个一级分类的二级分类
                List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                //2、封装上面的结果
                List<Catalog2Vo> catalog2Vos = null;
                if (categoryEntities != null) {
                    catalog2Vos = categoryEntities.stream().map(l2 -> {
                        Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                        //1、找当前二级分类的三级分类封装成vo
                        List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getCatId());
                        if (level3Catalog != null) {
                            List<Catalog2Vo.Catalog3Vo> catalog3VoList = level3Catalog.stream().map(l3 -> {
                                //2、封装成指定格式
                                Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                            catalog2Vo.setCatalog3List(catalog3VoList);
                        }
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return catalog2Vos;
            }));
            //3、查到的数据再放入缓存,将对象转为json放入缓存中,此时s是JSON格式
            String s = JSON.toJSONString(parent_cid);
            redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
            return parent_cid;
        }

    }

//    private List<CategoryEntity> getParent_cid(CategoryEntity v) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//    }

    /**
     * @param selectList 查询到的所有数据
     * @param parent_cid 查那个cid  ， 将cid作为父id进行检索
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {

        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        return collect;
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //从all菜单中里面找到root菜单
    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            //这里不能使用getParentCid() == 0;  因为int是-127-128 会出现越界
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //找到子菜单，这里使用了递归 getChildrens(..)会重新运行方法查找
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}