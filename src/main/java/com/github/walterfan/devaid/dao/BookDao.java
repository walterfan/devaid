package com.github.walterfan.devaid.dao;

import java.util.List;

import com.github.walterfan.devaid.domain.Book;
import com.github.walterfan.devaid.domain.BookSearchCriteria;
import com.github.walterfan.devaid.domain.BookVO;
import com.github.walterfan.devaid.domain.BorrowLog;


/**
 * @author walter
 *
 */
public interface BookDao extends ICRUD<Integer,Book> {
    public  int createBook(Book book);
    public  Book retrieveBook(int id);
    public  int updateBook(Book t);
    public int deleteBook(int id);
    
    public List<BookVO> listBook(int pageNo, int pageSize, BookSearchCriteria criteria);
    public List<BookVO> getUserBooks(int userID);
    public List<BookVO> getAllBooks();
    
    public int getBookCount(BookSearchCriteria criteria);
    public List<BookVO> getUserBorrowLogs(int userID);
    public List<BookVO> getBookBorrowLogs(int bookID);
    public int deleteBorrowLogs(int bookID);
    
    public int createBorrowLog(BorrowLog borrowLog);
    public BorrowLog retrieveBorrowLog(int id);
    public int updateBorrowLog(BorrowLog t);
    public int deleteBorrowLog(int id);

}
