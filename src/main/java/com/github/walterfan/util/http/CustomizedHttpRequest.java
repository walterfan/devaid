package com.github.walterfan.util.http;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CustomizedHttpRequest implements Comparable<CustomizedHttpRequest>, Iterable<String>{
	private String keyword;
	private boolean useRegex;
	private Set<String> varSet = new HashSet<String>();
	
	public void addVariable(String var) {
		varSet.add(var);
	}
	
	public boolean hasVariable() {
		return !varSet.isEmpty();
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public boolean isUseRegex() {
		return useRegex;
	}

	public void setUseRegex(boolean useRegex) {
		this.useRegex = useRegex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomizedHttpRequest other = (CustomizedHttpRequest) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "Keyword=" + keyword + ", useRegex="
				+ useRegex + ", varSet=" + varSet + "]";
	}

	public Iterator<String> iterator() {
		return this.varSet.iterator();
	}

	public int compareTo(CustomizedHttpRequest hr) {
		return this.keyword.compareTo(hr.getKeyword());
	}


	
}
