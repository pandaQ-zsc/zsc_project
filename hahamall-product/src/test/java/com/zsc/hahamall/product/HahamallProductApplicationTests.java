package com.zsc.hahamall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsc.hahamall.product.entity.BrandEntity;
import com.zsc.hahamall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HahamallProductApplicationTests {
	@Autowired
	BrandService brandService;

	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setBrandId(1l);
		brandEntity.setDescript("华为！");
//		brandEntity.setDescript("dream is possible");
//		brandEntity.setName("华为");
//		brandService.save(brandEntity);
//		System.out.println("保存成功.....");

//		brandService.updateById(brandEntity);
		List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		for (BrandEntity item : list) {
			System.out.println(item);
		}
	}

}
