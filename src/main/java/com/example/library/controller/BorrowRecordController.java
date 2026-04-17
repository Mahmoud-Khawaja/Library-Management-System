package com.example.library.controller;

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
    public ResponseEntity<BorrowRecordResponseDTO> borrowBook(
            @Valid @RequestBody BorrowRecordRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(borrowRecordService.borrowBook(dto));
    }

    @PutMapping("/api/borrow-records/{id}/return")
    public ResponseEntity<BorrowRecordResponseDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }

    // Both spellings supported: assignment PDF uses /api/borrowrecords (no hyphen) for this one endpoint
    @GetMapping({"/api/borrow-records/member/{memberId}", "/api/borrowrecords/member/{memberId}"})
    public ResponseEntity<List<BorrowRecordResponseDTO>> getBorrowRecordsByMember(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getBorrowRecordsByMember(memberId));
    }

    @GetMapping("/api/borrow-records/active")
    public ResponseEntity<List<BorrowRecordResponseDTO>> getActiveBorrowRecords() {
        return ResponseEntity.ok(borrowRecordService.getActiveBorrowRecords());
    }
}
