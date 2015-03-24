/**
 * 
 */
package com.github.walterfan.devaid.dao;

import java.util.List;

import com.github.walterfan.devaid.domain.Module;


/**
 * @author walter
 *
 */
public interface ModuleDao extends ICRUD<Integer,Module> {
	List<Module> getAllModuleList();

	List<Module> getModuleList(int roleID);
	
	List<Module> getFavoriteModules(int userID);
}
