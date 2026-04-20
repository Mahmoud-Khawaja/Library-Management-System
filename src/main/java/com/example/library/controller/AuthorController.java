package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.PaginatedResponse;
import com.example.library.dto.request.AuthorRequestDTO;
import com.example.library.dto.response.AuthorResponseDTO;
import com.example.library.dto.response.BookResponseDTO;
import com.example.library.service.AuthorService;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<AuthorResponseDTO>>> getAllAuthors(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<AuthorResponseDTO> authors = authorService.getAllAuthors(pageable);
        ApiResponse<PaginatedResponse<AuthorResponseDTO>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "Authors fetched successfully",
                PaginatedResponse.from(authors)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponseDTO>> getAuthorById(@PathVariable Long id) {
        AuthorResponseDTO author = authorService.getAuthorById(id);
        ApiResponse<AuthorResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Author fetched successfully", author);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponseDTO>> createAuthor(@Valid @RequestBody AuthorRequestDTO dto) {
        AuthorResponseDTO author = authorService.createAuthor(dto);
        ApiResponse<AuthorResponseDTO> response = ApiResponse.of(HttpStatus.CREATED.value(), "Author created successfully", author);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponseDTO>> updateAuthor(
            @PathVariable Long id, @Valid @RequestBody AuthorRequestDTO dto) {
        AuthorResponseDTO author = authorService.updateAuthor(id, dto);
        ApiResponse<AuthorResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Author updated successfully", author);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        ApiResponse<Void> response = ApiResponse.of(HttpStatus.OK.value(), "Author deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getBooksByAuthor(@PathVariable Long id) {
        List<BookResponseDTO> books = authorService.getBooksByAuthor(id);
        ApiResponse<List<BookResponseDTO>> response = ApiResponse.of(HttpStatus.OK.value(), "Author books fetched successfully", books);
        return ResponseEntity.ok(response);
    }
}
