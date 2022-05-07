package com.zsc.hahamall.search.service;

import com.zsc.hahamall.search.vo.SearchParam;
import com.zsc.hahamall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * <p>Title: MasllService</p>
 * Description：
 * date：2021/6/12 23:05
 */

public interface MallService {

	/**
	 * 检索所有参数
	 * 返回检索结果
	 */
	SearchResult search(SearchParam Param);
}
