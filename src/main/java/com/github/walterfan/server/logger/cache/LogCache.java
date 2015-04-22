package com.github.walterfan.server.logger.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.walterfan.server.logger.LogData;

/**
 * @author Walter Fan Ya Min
 *
 */
public class LogCache implements IDataCache<Long, LogData>{
	
	Map<Long,LogData> map = new HashMap<Long,LogData>();


	public LogData save(Long key, LogData data) {
		return map.put(key,data);

	}


	public LogData delete(Long key) {
		return map.remove(key);
	}


	public LogData retrieve(Long key) {
		return map.get(key);
	}


	public List<LogData> search(List<Long> keyList) {
		List<LogData> list = new LinkedList<LogData>();
		for(Long key:keyList) {
			LogData ld = map.get(key);
			if(ld!=null) {
				list.add(ld);
			}
		}
		return list;
	}



	public Map<Long, LogData> getData() {
		return map;
	}

}
