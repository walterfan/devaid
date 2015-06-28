package com.github.walterfan.devaid.webmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
*  list with fixed size and thread safe
* 
* @version 1.0 6/3/2008 
* @author Walter Fan Ya Min 
*/
public class FixedList<T> {
	private static Log logger = LogFactory.getLog(FixedList.class);				
	private int maxSize = 400;
	private int subSize = 20;
	private LinkedList<T> myList = new LinkedList<T>();
    //private LinkedList<T> myTmpList = new LinkedList<T>();
	private Lock lock = new ReentrantLock();
	private volatile boolean serializable = true;
	
	public int size() {
		lock.lock();
		try {
			return myList.size(); 
		} finally {
			lock.unlock();
		}
		
	}
	
	public void clear() {
		try {
			lock.lock();		
			myList.clear();			
		} finally {
			lock.unlock();
		}	
	}
		
	
	/*
	 * fetch data from list as descent according to pagenum pagenum [0,...]
	 */

	public List<T> getSubList(int pagenum) {
		int totalSize = this.size();
		int toIndex = totalSize - pagenum * subSize;
		int fromIndex = totalSize - (pagenum + 1) * subSize;		
		
		List<T> unSortList = getSubList(fromIndex, toIndex);
		if(unSortList != null) {
			List<T> toSortList = new ArrayList<T>(subSize); 
			toSortList.addAll(unSortList);
			Collections.reverse(toSortList);
			return toSortList;	
		}
		return null;
	}
	
	public List<T> getSubList(int fromIndex, int toIndex) {
		if (fromIndex < 0) {
			fromIndex = 0;
		}
							
		try {
			lock.lock();
			int totalSize = this.size();
	        //logger.debug("totalSize = " + totalSize);
			if (toIndex > totalSize) {
				toIndex = totalSize;
			}
			if (fromIndex >= toIndex) {
				return null;
			} else {
				return myList.subList(fromIndex, toIndex);
			}
		} finally {
			lock.unlock();
		}
	}
	
	
    public void addElement(T element) {                
		try {
			lock.lock();
			if (this.size() == this.maxSize) {
				myList.removeFirst();
			}
			myList.addLast(element);
		} finally {
        	lock.unlock();
        }
        if(serializable) {
            logger.trace(element);
        }
    }
    
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		assert (maxSize > 0);
		this.maxSize = maxSize;
	}

	public int getSubSize() {
		return subSize;
	}

	public void setSubSize(int subSize) {
		assert (subSize > 0);
		this.subSize = subSize;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("Fixed list size = " + myList.size() + "\n");
		int i = 0;
		for (T info : myList) {
			sb.append(i + ". " + info.toString() + "\n");
		}
		return sb.toString();
	}
	
	public boolean isEmpty() {
		return this.myList.isEmpty();
	}
}
