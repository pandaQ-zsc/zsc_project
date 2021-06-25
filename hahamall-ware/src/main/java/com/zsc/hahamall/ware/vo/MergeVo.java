package com.zsc.hahamall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    //整单id
    private Long purchaseId;
    //合并项集合
    private List<Long> items;
}
