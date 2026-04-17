package com.example.library.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BorrowRecordResponseDTO {
    private Long id;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private BookResponseDTO book;
    private MemberResponseDTO member;
}
