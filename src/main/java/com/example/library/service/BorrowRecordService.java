package com.example.library.service;

import com.example.library.dto.request.BorrowRecordRequestDTO;
import com.example.library.dto.response.BorrowRecordResponseDTO;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Member;
import com.example.library.exception.BookAlreadyBorrowedException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordMapper borrowRecordMapper;

    @Transactional
    public BorrowRecordResponseDTO borrowBook(BorrowRecordRequestDTO dto) {
        // Load book with author eagerly to avoid lazy-loading issues during response mapping
        Book book = bookRepository.findByIdWithAuthor(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", dto.getBookId()));

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        borrowRecordRepository.findActiveByBookId(dto.getBookId()).ifPresent(existing -> {
            throw new BookAlreadyBorrowedException(
                    "Book with id " + dto.getBookId() + " is already borrowed and has not been returned yet."
            );
        });

        BorrowRecord record = BorrowRecord.builder()
                .book(book)
                .member(member)
                .build();

        BorrowRecord saved = borrowRecordRepository.save(record);
        // Reload with JOIN FETCH so book.author is not a lazy proxy during mapping
        return borrowRecordMapper.toResponseDTO(
                borrowRecordRepository.findByIdWithDetails(saved.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", saved.getId()))
        );
    }

    @Transactional
    public BorrowRecordResponseDTO returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", id));

        if (record.getReturnDate() != null) {
            throw new IllegalArgumentException("This borrow record has already been returned on " + record.getReturnDate());
        }

        record.setReturnDate(LocalDate.now());
        borrowRecordRepository.save(record);

        // Reload after save to get a clean mapped response
        return borrowRecordMapper.toResponseDTO(
                borrowRecordRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", id))
        );
    }

    public List<BorrowRecordResponseDTO> getBorrowRecordsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member", memberId);
        }
        return borrowRecordRepository.findByMemberIdWithDetails(memberId).stream()
                .map(borrowRecordMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordResponseDTO> getActiveBorrowRecords() {
        return borrowRecordRepository.findActiveWithDetails().stream()
                .map(borrowRecordMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
