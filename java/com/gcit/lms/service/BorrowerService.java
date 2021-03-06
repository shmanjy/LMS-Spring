/**
 * 
 */
package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;

/**
 * @author tejassrinivas
 *
 */
@Transactional
public class BorrowerService {
	
	@Autowired
	BranchDAO brDao;
	
	@Autowired
	BookDAO bDao;
	
	@Autowired
	BookCopiesDAO bcDao;
	
	@Autowired
	BookLoansDAO blDao;
	
	@Autowired
	BorrowerDAO brwDao;
	
	public boolean checkValidation(Integer cardNo) {
		Borrower borrower = new Borrower();
		System.out.println("Entering inside borrower card validation");	
		borrower.setCardNo(cardNo);
		System.out.println("Borrower card No " + borrower.getCardNo());
		boolean valFlag =  false;
		try {
			valFlag = brwDao.checkValidation(borrower);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return valFlag;
	}
	
	public List<Branch> readAllBranches()  {
		List<Branch> branches = null;
		try {
			branches = brDao.readAllBranches();
			for(Branch br : branches) {
				try {
					br.setBooks(bDao.readBooksByBranches(br));
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return branches;
	}
	
	public List<Book> readAllBooksInBranch(Integer branchId) {
		List<Book> books = null;
		try {
			Branch branch = brDao.readBranchByPK(branchId);
			books = bDao.readBooksByBranches(branch) ;
			for(Book b : books) {
				b.setBookCopies(bcDao.readBookCopieForaBook(b.getBookId()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}	
		return books;
	}
	
	public List<Book> readBooksFromLoans(Integer branchId, Integer cardNo)  {
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		Borrower borrower = new Borrower();
		borrower.setCardNo(cardNo);
		List<Book> books = null;
		try {
			books = bDao.readBooksByBorrowers(branch, borrower);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return books;
	}
	
	public void checkOutFucntion(Integer cardNo, Integer bookId, Integer branchId) {
		BookLoans bookLoans = new BookLoans();
		BookCopies bookCopies = new BookCopies();
		bookLoans.setCardNo(cardNo);
		bookLoans.setBookId(bookId);
		bookLoans.setBranchId(branchId);
		bookCopies.setBookId(bookId);
		bookCopies.setBranchId(branchId);		
		try {
			bcDao.updateBookCopies(bookCopies);
			blDao.addBookLoans(bookLoans);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Integer countOfLoanedBooksByBorrower(Integer bookId, Integer cardNo, Integer branchId)  {
		BookLoans bookLoan = new BookLoans();
		bookLoan.setBranchId(branchId);
		bookLoan.setBookId(bookId);
		bookLoan.setCardNo(cardNo);
		Integer noOfCopies = 0;
		try {
			noOfCopies = blDao.loanedBooksByBorrower(bookLoan);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return noOfCopies;
	}
	
	public void updateLoanTablesEnrty(Integer cardNo, Integer bookId, Integer branchId) {
		BookLoans bookLoans = new BookLoans();
		BookCopies bookCopies = new BookCopies(); 
		bookLoans.setBranchId(branchId);
		bookLoans.setCardNo(cardNo);
		bookLoans.setBookId(bookId);
		bookCopies.setBookId(bookId);
		bookCopies.setBranchId(branchId);
		try {
			blDao.updateBookLoans(bookLoans);
			bcDao.updateLoanBookCopies(bookCopies);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
