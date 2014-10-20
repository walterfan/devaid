package com.github.walterfan.devaid.sql;

import javax.swing.table.*;
import javax.swing.event.*;


/**
 * @author walter
 *
 */
public class KeyValueTableModel implements TableModel {
    private Object value = 0;
    private String key ;
       
    public KeyValueTableModel(String name, Object value) {
        this.value = value;
        this.key = name;
    }
    
    public void addTableModelListener(TableModelListener tablemodellistener) {
        // TODO Auto-generated method stub
        
    }

    
    public Class<?> getColumnClass(int i) {
        // TODO Auto-generated method stub
        return String.class;
    }

    
    public int getColumnCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    
    public String getColumnName(int i) {
        // TODO Auto-generated method stub
        return key;
    }

    
    public int getRowCount() {
        return 1;
    }

    
    public Object getValueAt(int i, int j) {
        // TODO Auto-generated method stub
        return value;
    }

    
    public boolean isCellEditable(int i, int j) {
        // TODO Auto-generated method stub
        return false;
    }

    
    public void removeTableModelListener(TableModelListener tablemodellistener) {
        // TODO Auto-generated method stub
        
    }

    
    public void setValueAt(Object obj, int i, int j) {
        // TODO Auto-generated method stub
        
    }
    
}
