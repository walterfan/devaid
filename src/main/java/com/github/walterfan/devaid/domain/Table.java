package com.github.walterfan.devaid.domain;

import java.util.Map;

public class Table extends BaseObject {
	private String space;
	private String table;
	private String comments;
	
	private Map<String, Column> columns;
}
