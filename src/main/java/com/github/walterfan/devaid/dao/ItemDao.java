package com.github.walterfan.devaid.dao;

import java.util.List;
import java.util.Map;

import com.github.walterfan.devaid.domain.Item;
import com.github.walterfan.devaid.domain.UserCategory;



public interface ItemDao extends ICRUD<Integer, Item>{
	List<Item> findItems(Item item);
	
	List<Item> getCheckList(int userID);
	
	UserCategory getCategory(int categoryID);

	Map<Integer, UserCategory> getCategories(int categoryType,int userID);

	List<UserCategory> getCategories(int userID);
	
	List<UserCategory> getCategoryList(int categoryType, int userID) ;
	
    int createCategory(UserCategory category);
    
    int updateCategory(UserCategory category);
    
    int deleteCategory(int categoryID);
}
