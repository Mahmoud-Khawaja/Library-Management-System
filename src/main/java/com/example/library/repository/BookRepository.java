package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // JOIN FETCH resolves the N+1 problem when loading books list with author details
    @Query(value = "SELECT b FROM Book b JOIN FETCH b.author",
           countQuery = "SELECT COUNT(b) FROM Book b")
    Page<Book> findAllWithAuthorPageable(Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.author.id = :authorId")
    List<Book> findByAuthorIdWithAuthor(@Param("authorId") Long authorId);

    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:genre IS NULL OR LOWER(b.genre) = LOWER(:genre)) AND " +
           "(:publishedYear IS NULL OR b.publishedYear = :publishedYear)")
    List<Book> searchBooks(@Param("title") String title,
                           @Param("genre") String genre,
                           @Param("publishedYear") Integer publishedYear);
}
