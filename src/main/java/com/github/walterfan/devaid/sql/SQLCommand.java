package com.github.walterfan.devaid.sql;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.walterfan.util.http.HttpCommand;


class SQLCommands implements Iterable<Map.Entry<String, SQLCommand>>{
    String type;
    Map<String, SQLCommand> sqlMap = new HashMap<String,SQLCommand>();
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the sqlList
     */
    public Map<String,SQLCommand> getSqlMap() {
        return sqlMap;
    }
    
    public SQLCommand getSQLCommand(String name) {
        return sqlMap.get(name);
    }
    
    public void addSQLCommand(SQLCommand s) {
        this.sqlMap.put(s.getName(),s);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("type=" + type);
        for(SQLCommand cmd: sqlMap.values()) {
            sb.append(cmd + "\n");
        }
        return sb.toString();
    }

	public Iterator<Entry<String, SQLCommand>> iterator() {
		return sqlMap.entrySet().iterator();
	}
    
}

public class SQLCommand implements Comparable<SQLCommand> {
    String name;
    String sql;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the sql
     */
    public String getSql() {
        return sql;
    }
    
    /**
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public String toString() {
        return "name=" + name + ", sql=" + sql;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sql == null) ? 0 : sql.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SQLCommand other = (SQLCommand) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sql == null) {
            if (other.sql != null)
                return false;
        } else if (!sql.equals(other.sql))
            return false;
        return true;
    }

    public int compareTo(SQLCommand arg0) {
        return name.compareTo(arg0.getName());
    }
    
}
