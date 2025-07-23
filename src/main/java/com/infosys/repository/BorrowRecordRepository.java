package com.infosys.repository;

import com.infosys.beans.Book;
import com.infosys.beans.BorrowRecord;
import com.infosys.dto.response.BookBorrowStats;
import com.infosys.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = 'ACTIVE' ")
    int countBorrowedBooks();

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = 'OVERDUE' ")
    int countOverdueBooks();


    @Query("SELECT NEW com.infosys.dto.response.BookBorrowStats(br.book.id, COUNT(br)) " +
            "FROM BorrowRecord br GROUP BY br.book.id ORDER BY COUNT(br) DESC")
    List<BookBorrowStats> findPopularBooks(int limit);

    List<BorrowRecord> findByUser_Id(Integer userId);

    @Query("SELECT SUM(b.fineAmount) FROM BorrowRecord b")
    Long getTotalFineAmount();



    List<BorrowRecord> findByBook_Id(Integer bookId);

    List<BorrowRecord> findByStatus(BorrowStatus status);

    List<BorrowRecord> findByUser_IdAndStatus(Integer userId, BorrowStatus status);

    List<BorrowRecord> findByDueDateBeforeAndStatus(LocalDate date, BorrowStatus status);
    List<BorrowRecord> findAllByOrderByBorrowDateDescIdDesc();

    @Query("SELECT b FROM Book b " +
            "JOIN BorrowRecord br ON br.book = b " +
            "GROUP BY b " +
            "ORDER BY COUNT(br) DESC")
    List<Book> findMostBorrowedBooks();

    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = :status")
    List<BorrowRecord> findActiveRecordsByUser(@Param("userId") Integer userId, @Param("status") BorrowStatus status);


    @Query("SELECT CASE WHEN COUNT(br) > 0 THEN true ELSE false END " +
            "FROM BorrowRecord br WHERE br.user.id = :userId AND br.book.id = :bookId AND br.status = :status")
    boolean existsByUserIdAndBookIdAndStatus(@Param("userId") Integer userId,
                                             @Param("bookId") Integer bookId,
                                             @Param("status") BorrowStatus status);

    @Query("SELECT SUM(b.fineAmount) FROM BorrowRecord b WHERE b.user.id = :userId")
    Long getTotalFineByUserId(@Param("userId") Integer userId);
}