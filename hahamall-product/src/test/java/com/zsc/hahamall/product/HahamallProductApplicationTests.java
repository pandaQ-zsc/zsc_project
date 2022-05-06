package com.zsc.hahamall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsc.hahamall.product.entity.BrandEntity;
import com.zsc.hahamall.product.service.BrandService;
import com.zsc.hahamall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HahamallProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redisson;

    @Test
    public void redisson() {
        System.out.println(redisson);
    }

    @Test
    public void testStringRedisTemplate() {
        //hello world
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world_" + UUID.randomUUID().toString());
        String hello = ops.get("hello");
        System.out.println(hello);

    }


    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

//
//    @Autowired
//    OSSClient ossClient;

//    @Test
//    void testUpLoad() {
//        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
//// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tFJb89A4yKKZZNq558U";
//        String accessKeySecret = "Q0w76qD0JayUKRgTZlf6asAyGFUGHV";
//
//// 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 创建PutObjectRequest对象。
//// 填写Bucket名称、Object完整路径和本地文件的完整路径。Object完整路径中不能包含Bucket名称。
//// 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
//        PutObjectRequest putObjectRequest = new PutObjectRequest("hahamall-hello", "aunty.jpg", new File("C:\\Users\\xxx\\Pictures\\aunty.jpg"));
//
//// 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
//        metadata.setObjectAcl(CannedAccessControlList.Private);
//        putObjectRequest.setMetadata(metadata);
//
//// 上传文件。
//        ossClient.putObject(putObjectRequest);
//
//// 关闭OSSClient。
//        ossClient.shutdown();
//    }

    @Test
    public void contextLoads() {
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
