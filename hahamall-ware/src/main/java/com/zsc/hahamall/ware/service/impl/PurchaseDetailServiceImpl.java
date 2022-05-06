package com.zsc.hahamall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsc.common.utils.PageUtils;
import com.zsc.common.utils.Query;

import com.zsc.hahamall.ware.dao.PurchaseDetailDao;
import com.zsc.hahamall.ware.entity.PurchaseDetailEntity;
import com.zsc.hahamall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        String  key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w->{ w.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }

        String  status = (String) params.get("status");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("status",status);
        }

        String  wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据采购单id purchase_id 查找到id  然后更新id对应的status
     * @param id
     * @return
     */
    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> purchaseId = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return purchaseId;
    }

}