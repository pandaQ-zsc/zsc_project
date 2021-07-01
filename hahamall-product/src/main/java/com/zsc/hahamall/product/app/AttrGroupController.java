package com.zsc.hahamall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zsc.hahamall.product.entity.AttrEntity;
import com.zsc.hahamall.product.service.AttrAttrgroupRelationService;
import com.zsc.hahamall.product.service.AttrService;
import com.zsc.hahamall.product.service.CategoryService;
import com.zsc.hahamall.product.vo.AttrGroupRelationVo;
import com.zsc.hahamall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zsc.hahamall.product.entity.AttrGroupEntity;
import com.zsc.hahamall.product.service.AttrGroupService;
import com.zsc.common.utils.PageUtils;
import com.zsc.common.utils.R;


/**
 * 属性分组
 *
 * @author zsc_xq
 * @email zsc_xq@gmail.com
 * @date 2021-05-19 21:40:30
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;
    @Autowired
    AttrAttrgroupRelationService relationService;

    ///product/attrgroup/attr/relation

    /**
     * [{
     *   "attrGroupId": 0, //分组id
     *   "attrId": 0, //属性id
     * }]
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();

    }
    ///product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId")Long catelogId){
        //1、查出当前分类下的所有属性分组，
        //2、查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);
    }

    // /product/attrgroup/{attrgroupId}/attr/relation    ---查询attr关联的属性
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List <AttrEntity>entities = attrService.getRelationAttr(attrgroupId);
        return  R.ok().put("data",entities);
    }

    ///product/attrgroup/{attrgroupId}/noattr/relation   --查询attr没有关联的属性
    //   @RequestParam Map<String, Object> params  分页参数的所有信息
    //   @RequestParam用于获取查询参数。
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params) {
        //由于接口文档的响应数据 因此 需要返回PageUtils格式的数据
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        //接口文档中page:{}
        return R.ok().put("page", page);

    }



    ///product/attrgroup/attr/relation/delete
    //[{"attrId":1,"attrGroupId":2}]
    @PostMapping("/attr/relation/delete")
    // 【以为post请求会携带来json的数据因此需要在参数系列表中使用@RequestBody 才能接受到post请求的数据  】
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return  R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId) {
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page =attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path= categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //  @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //  @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
