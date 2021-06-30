package com.zsc.hahamall.product.vo;

import com.zsc.hahamall.product.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2Vo {
    private String catalog1Id;   //1级父分类id
    private List<Catalog3Vo> catalog3List; //三级子分类
    private String id;
    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catalog3Vo {
        private String catalog2Id; //父分类，2级分类id
        private String id;
        private String name;


    }
}


