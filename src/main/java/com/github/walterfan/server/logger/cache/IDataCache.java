package com.github.walterfan.server.logger.cache;

import java.util.List;
import java.util.Map;

import com.github.walterfan.server.logger.LogData;

public interface IDataCache<K,V> {
	
	V save(K key, V data);
	
	V retrieve(K key);
	
	V delete(K key);
	
	List<V> search(List<K> keyList);
	
	Map<Long, LogData> getData();
}
