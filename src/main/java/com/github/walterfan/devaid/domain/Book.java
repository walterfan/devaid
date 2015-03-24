package com.github.walterfan.devaid.domain;

import java.sql.Timestamp;

/**
 * @author walter
 * 
 */
public class Book extends BaseObject {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1156666440406865554L;
    private int bookID;
    private int borrowLogID;
    private String bookSN;
    private String bookName;
    private String tags;
    private int categoryID;

    private Timestamp createTime;

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookSN() {
        return bookSN;
    }

    public void setBookSN(String bookSN) {
        this.bookSN = bookSN;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getBorrowLogID() {
        return borrowLogID;
    }

    public void setBorrowLogID(int borrowLogID) {
        this.borrowLogID = borrowLogID;
    }

}
