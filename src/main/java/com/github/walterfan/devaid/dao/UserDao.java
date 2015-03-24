/**
 * 
 */
package com.github.walterfan.devaid.dao;

import java.util.List;

import com.github.walterfan.devaid.domain.EmailTask;
import com.github.walterfan.devaid.domain.User;



/**
 * @author walter
 *
 */
public interface UserDao extends ICRUD<Integer,User> {
	
    int fetchEmailTaskList(String executor, int count, int maxDuration);
    
    List<EmailTask> getEmailTaskList(String status, int count, String executor);
	   
	int createEmailTask(EmailTask et);
	
	EmailTask retrieveEmailTask(int etID);
	
	int updateEmailTask(EmailTask et);
	
	int deleteEmailTask(int etID);
	
	User findUserByName(String userName);
}
