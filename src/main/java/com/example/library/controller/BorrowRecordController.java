package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.request.BorrowRecordRequestDTO;
import com.example.library.dto.response.BorrowRecordResponseDTO;
import com.example.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @PostMapping("/api/borrow-records")
    public ResponseEntity<ApiResponse<BorrowRecordResponseDTO>> borrowBook(
            @Valid @RequestBody BorrowRecordRequestDTO dto) {
        BorrowRecordResponseDTO borrowRecord = borrowRecordService.borrowBook(dto);
        ApiResponse<BorrowRecordResponseDTO> response = ApiResponse.of(HttpStatus.CREATED.value(), "Book borrowed successfully", borrowRecord);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/api/borrow-records/{id}/return")
    public ResponseEntity<ApiResponse<BorrowRecordResponseDTO>> returnBook(@PathVariable Long id) {
        BorrowRecordResponseDTO borrowRecord = borrowRecordService.returnBook(id);
        ApiResponse<BorrowRecordResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Book returned successfully", borrowRecord);
        return ResponseEntity.ok(response);
    }

    // Both spellings supported: assignment PDF uses /api/borrowrecords (no hyphen) for this one endpoint
    @GetMapping({"/api/borrow-records/member/{memberId}", "/api/borrowrecords/member/{memberId}"})
    public ResponseEntity<ApiResponse<List<BorrowRecordResponseDTO>>> getBorrowRecordsByMember(
            @PathVariable Long memberId) {
        List<BorrowRecordResponseDTO> borrowRecords = borrowRecordService.getBorrowRecordsByMember(memberId);
        ApiResponse<List<BorrowRecordResponseDTO>> response = ApiResponse.of(HttpStatus.OK.value(), "Borrow records fetched successfully", borrowRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/borrow-records/active")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponseDTO>>> getActiveBorrowRecords() {
        List<BorrowRecordResponseDTO> activeBorrowRecords = borrowRecordService.getActiveBorrowRecords();
        ApiResponse<List<BorrowRecordResponseDTO>> response = ApiResponse.of(HttpStatus.OK.value(), "Active borrow records fetched successfully", activeBorrowRecords);
        return ResponseEntity.ok(response);
    }
}
