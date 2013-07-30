package com.github.walterfan.util.db;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * db help class
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public final class DbHelper {
    
    private DbHelper() {
        
    }
    /**
     * @param <T> any collection
     * @param list such as confid list {11,222,333}
     * @param separator such as ,
     * @return 11,222,3s33
     */
    public static <T> String list2String(final Collection<T> list, final String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");
        Iterator<T> iter = list.iterator();
        sb.append(iter.next());
        for (; iter.hasNext(); ) {
            T t = iter.next();
            sb.append(separator);
            sb.append(t);
        }
        return sb.toString();
    }

    /**
     * @param list
     *            confid list
     * @return confid list string separated with ,
     */
    public static String list2String(final Collection<Long> list) {
        return list2String(list, ",");
    }

    /**
     * @param rs
     *            rsultset
     * @param output
     *            output stream
     * @throws SQLException
     *             if resultset read error
     */
    public static void printResultsTable(ResultSet rs, OutputStream output) throws SQLException {
        PrintWriter out = new PrintWriter(output);
        ResultSetMetaData metadata = rs.getMetaData();
        int numcols = metadata.getColumnCount(); // how many columns
        String[] labels = new String[numcols]; // the column labels
        int[] colwidths = new int[numcols]; // the width of each
        int[] colpos = new int[numcols]; // start position of each
        int linewidth; // total width of table
        linewidth = 1; // for the initial '|'.
        for (int i = 0; i < numcols; i++) { // for each column
            colpos[i] = linewidth; // save its position
            labels[i] = metadata.getColumnLabel(i + 1); // get its label
            int size = metadata.getColumnDisplaySize(i + 1);
            if (size == -1) {
                size = 30; // Some driver return -1...
            }
            if (size > 500) {
                size = 30; // Don't allow unreasonable sizes
            }
            int labelsize = labels[i].length();
            if (labelsize > size) {
                size = labelsize;
            }
            colwidths[i] = size + 1; // save the column the size
            linewidth += colwidths[i] + 2; // increment total size
        }
        StringBuffer divider = new StringBuffer(linewidth);
        StringBuffer blankline = new StringBuffer(linewidth);
        for (int i = 0; i < linewidth; i++) {
            divider.insert(i, '-');
            blankline.insert(i, " ");
        }
        // Put special marks in the divider line at the column positions
        for (int i = 0; i < numcols; i++) {
            divider.setCharAt(colpos[i] - 1, '+');
        }
        divider.setCharAt(linewidth - 1, '+');
        out.println(divider);
        StringBuffer line = new StringBuffer(blankline.toString());
        line.setCharAt(0, '|');
        for (int i = 0; i < numcols; i++) {
            int pos = colpos[i] + 1 + (colwidths[i] - labels[i].length()) / 2;
            overwrite(line, pos, labels[i]);
            overwrite(line, colpos[i] + colwidths[i], " |");
        }
        out.println(line);
        out.println(divider);
        while (rs.next()) {
            line = new StringBuffer(blankline.toString());
            line.setCharAt(0, '|');
            for (int i = 0; i < numcols; i++) {
                Object value = rs.getObject(i + 1);
                if (value != null) {
                    overwrite(line, colpos[i] + 1, value.toString().trim());
                }
                overwrite(line, colpos[i] + colwidths[i], " |");
            }
            out.println(line);
        }
        out.println(divider);
        out.flush();
    }

    /**
     * @param b String buffer
     * @param pos position
     * @param s string
     */
    private static void overwrite(StringBuffer b, int pos, String s) {
        int slen = s.length(); // string length
        int blen = b.length(); // buffer length
        if (pos + slen > blen) {
            slen = blen - pos; // does it fit?
        }
        for (int i = 0; i < slen; i++) {
            // copy string into buffer
            b.setCharAt(pos + i, s.charAt(i));
        }
    }

    /**
     * @param args none
     */
    public static void main(String[] args) {
        List<Long> list = new java.util.ArrayList<Long>();
        for (long i = 0; i < 10; i++) {
            list.add(i);
        }
        System.out.println(list2String(list));

    }

}
