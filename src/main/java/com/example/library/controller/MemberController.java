package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.PaginatedResponse;
import com.example.library.dto.request.MemberRequestDTO;
import com.example.library.dto.response.MemberResponseDTO;
import com.example.library.service.MemberService;
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
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<MemberResponseDTO>>> getAllMembers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<MemberResponseDTO> members = memberService.getAllMembers(pageable);
        ApiResponse<PaginatedResponse<MemberResponseDTO>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "Members fetched successfully",
                PaginatedResponse.from(members)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> getMemberById(@PathVariable Long id) {
        MemberResponseDTO member = memberService.getMemberById(id);
        ApiResponse<MemberResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Member fetched successfully", member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> searchMembers(@RequestParam String name) {
        List<MemberResponseDTO> members = memberService.searchMembers(name);
        ApiResponse<List<MemberResponseDTO>> response = ApiResponse.of(HttpStatus.OK.value(), "Members fetched successfully", members);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponseDTO>> createMember(@Valid @RequestBody MemberRequestDTO dto) {
        MemberResponseDTO member = memberService.createMember(dto);
        ApiResponse<MemberResponseDTO> response = ApiResponse.of(HttpStatus.CREATED.value(), "Member created successfully", member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMember(
            @PathVariable Long id, @Valid @RequestBody MemberRequestDTO dto) {
        MemberResponseDTO member = memberService.updateMember(id, dto);
        ApiResponse<MemberResponseDTO> response = ApiResponse.of(HttpStatus.OK.value(), "Member updated successfully", member);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        ApiResponse<Void> response = ApiResponse.of(HttpStatus.OK.value(), "Member deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
