package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.request.BookRequestDTO;
import com.example.library.dto.response.BookResponseDTO;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> getAllBooks(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<BookResponseDTO> books = bookService.getAllBooks(pageable);
        ApiResponse<Page<BookResponseDTO>> response = ApiResponse.of(HttpStatus.OK.value(), "Books fetched successfully", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        ApiResponse<BookResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Book fetched successfully", book);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponseDTO>> createBook(@Valid @RequestBody BookRequestDTO dto) {
        BookResponseDTO book = bookService.createBook(dto);
        ApiResponse<BookResponseDTO> response = ApiResponse.of(HttpStatus.CREATED.value(), "Book created successfully", book);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> updateBook(
            @PathVariable Long id, @Valid @RequestBody BookRequestDTO dto) {
        BookResponseDTO book = bookService.updateBook(id, dto);
        ApiResponse<BookResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Book updated successfully", book);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        ApiResponse<Void> response = ApiResponse.of(HttpStatus.OK.value(), "Book deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer publishedYear) {
        List<BookResponseDTO> books = bookService.searchBooks(title, genre, publishedYear);
        ApiResponse<List<BookResponseDTO>> response = ApiResponse.of(HttpStatus.OK.value(), "Books fetched successfully", books);
        return ResponseEntity.ok(response);
    }
}
