package com.github.walterfan.server.logger.cache;

/**
 * 
 * synchronize data from cache to DB
 * 
 * remove data from cache
 * 
 * 
 * @author Walter Fan Ya Min
 *
 */
public interface CacheSynchronizer {
	/**
	 * Firstly, check which data changed from last checking time
	 * Then, synchronize changed data to DB
	 *  
	 */
	void synchronize();
	
	
	/**
	 * Firstly, check which data is unnecessary
	 * Then, synchronize changed data to DB and remove unnecessary data from cache 
	 */
	void remove();
}
