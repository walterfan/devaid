package com.github.walterfan.devaid.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ResultTableModel implements TableModel {
	//private Object[][] contents;
	private String[] columnNames;
	private Class[] columnClasses;
	List<Object[]> rows = new ArrayList<Object[]>(30);
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}
	
	public ResultTableModel(ResultSet rs) throws SQLException {
        //PrintWriter out = new PrintWriter(output);
        ResultSetMetaData metadata = rs.getMetaData();
        int numcols = metadata.getColumnCount(); // how many columns
        columnNames = new String[numcols]; // the column labels
        columnClasses = new Class[numcols];
        
        for (int i = 0; i < numcols; i++) { // for each column
            columnNames[i] = metadata.getColumnLabel(i + 1); // get its label
            try {
            	String clsName = metadata.getColumnClassName(i+1);
            	columnClasses[i] = Class.forName(metadata.getColumnClassName(i+1));
			} catch (ClassNotFoundException e) {
				//throw new SQLException(e.getMessage());
				columnClasses[i] = java.lang.Object.class;
			}
            int size = metadata.getColumnDisplaySize(i + 1);
            if (size == -1) {
                size = 30; // Some driver return -1...
            }
            if (size > 500) {
                size = 30; // Don't allow unreasonable sizes
            }
            int labelsize = columnNames[i].length();
            if (labelsize > size) {
                size = labelsize;
            }
        }

        

        while (rs.next()) {
        	Object[] row = new Object[numcols];
            for (int j = 0; j < numcols; j++) {
                Object value = rs.getObject(j + 1);
                if (value != null) {
                	row[j]= value;
                }
            }
            rows.add(row);

        }
       
    }

	
	public Class<?> getColumnClass(int columnIndex) {
		
		return columnClasses[columnIndex];
	}

	public int getColumnCount() {
		
		return columnClasses.length;
	}

	public String getColumnName(int columnIndex) {
		
		return columnNames[columnIndex];
	}

	public int getRowCount() {
		return rows.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return rows.get(rowIndex)[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
