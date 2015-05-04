package com.github.walterfan.devaid.mybatis;

import com.github.walterfan.devaid.domain.Category;
import com.github.walterfan.devaid.domain.Link;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class LinkService {
		 
	    @Autowired
	    private LinkMapper mapper;
	 
	    public List<Link> selectAllLink(){
	        return mapper.selectAllLink();
	    }
	 
	    public Link selectLink(int id){
	        return mapper.selectLink(id);
	    }
	 
	    public int insertLink(Link parent){
	        return mapper.insertLink(parent);
	    }
	 
	    public List<Category> selectAllCategories(int parnetId){
	        return mapper.selectAllCategories(parnetId);
	    }
	 
	    public Category selectCategory(int id){
	        return mapper.selectCategory(id);
	    }
	 
	    public int insertCategory(Category child){
	        return mapper.insertCategory(child);
	    }
	}
