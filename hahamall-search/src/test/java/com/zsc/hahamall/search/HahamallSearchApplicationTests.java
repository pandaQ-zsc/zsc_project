package com.zsc.hahamall.search;

import com.alibaba.fastjson.JSON;
import com.zsc.hahamall.search.config.HahamallElasticSearchConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HahamallSearchApplicationTests {
	@Autowired
	private RestHighLevelClient client;
	@Test
	public void contextLoads() {
		System.out.println(client);
	}
	@Test
	public void indexData() throws IOException {

		// 设置索引
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1"); //数据的id
		User user = new User();
		user.setUserName("张三");
		user.setAge(20);
		user.setGender("男");
		String jsonString = JSON.toJSONString(user);
		//设置要保存的内容，指定数据和类型
		indexRequest.source(jsonString, XContentType.JSON);

		//执行创建索引和保存数据
		IndexResponse index = client.index(indexRequest, HahamallElasticSearchConfig.COMMON_OPTIONS);

		System.out.println(index);

	}
	@Test
	public void find() throws IOException {
		// 1 创建检索请求
		SearchRequest searchRequest = new SearchRequest();
		//指定索引（database）
		searchRequest.indices("bank");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// 构造检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregation();
		sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
		System.out.println(sourceBuilder.toString());

		searchRequest.source(sourceBuilder);

		// 2 执行检索
		SearchResponse response = client.search(searchRequest, HahamallElasticSearchConfig.COMMON_OPTIONS);
		// 3 分析响应结果
		System.out.println(response.toString());
	}


	@Data
	class User{
		private String userName;
		private String gender;
		private Integer age;

	}

}
