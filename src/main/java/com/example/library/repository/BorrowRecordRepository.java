package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("SELECT br FROM BorrowRecord br " +
           "JOIN FETCH br.book b JOIN FETCH b.author " +
           "JOIN FETCH br.member " +
           "WHERE br.id = :id")
    Optional<BorrowRecord> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT br FROM BorrowRecord br " +
           "JOIN FETCH br.book b JOIN FETCH b.author " +
           "JOIN FETCH br.member " +
           "WHERE br.member.id = :memberId")
    List<BorrowRecord> findByMemberIdWithDetails(@Param("memberId") Long memberId);

    @Query("SELECT br FROM BorrowRecord br " +
           "JOIN FETCH br.book b JOIN FETCH b.author " +
           "JOIN FETCH br.member " +
           "WHERE br.returnDate IS NULL")
    List<BorrowRecord> findActiveWithDetails();

    @Query("SELECT br FROM BorrowRecord br WHERE br.book.id = :bookId AND br.returnDate IS NULL")
    Optional<BorrowRecord> findActiveByBookId(@Param("bookId") Long bookId);
}
