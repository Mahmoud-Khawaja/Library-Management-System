package com.example.library.service;

import com.example.library.dto.request.BookRequestDTO;
import com.example.library.dto.response.BookResponseDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        // Uses JOIN FETCH to prevent N+1: without it, each Book would trigger
        // a separate SELECT to load its lazy Author relationship.
        return bookRepository.findAllWithAuthorPageable(pageable).map(bookMapper::toResponseDTO);
    }

    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        return bookMapper.toResponseDTO(book);
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", dto.getAuthorId()));
        Book book = bookMapper.toEntity(dto);
        book.setAuthor(author);
        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", dto.getAuthorId()));
        bookMapper.updateEntityFromDTO(dto, book);
        book.setAuthor(author);
        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", id);
        }
        bookRepository.deleteById(id);
    }

    public List<BookResponseDTO> searchBooks(String title, String genre, Integer publishedYear) {
        return bookRepository.searchBooks(title, genre, publishedYear).stream()
                .map(bookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
