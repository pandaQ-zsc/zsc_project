package com.zsc.hahamall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zsc.common.to.es.SkuEsModel;
import com.zsc.hahamall.search.config.HahamallElasticSearchConfig;
import com.zsc.hahamall.search.constant.EsConstant;
import com.zsc.hahamall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到es
        //1、给es中建立索引，product-mapping.txt。简历好映射关系
        BulkRequest bulkRequest = new BulkRequest();
        //2、给es中保存这些数据
        for (SkuEsModel model : skuEsModels) {
            //1、构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //指定index中数据的id
            indexRequest.id(model.getSkuId().toString());
            //将model对象转成JSON字符串
            String s   = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, HahamallElasticSearchConfig.COMMON_OPTIONS);
        //TODO es请求出错处理
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            //找到那些id的请求出错
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成：{},返回数据:{}",collect,bulk.toString());
        return  b;
    }
}
