package com.example.library.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate membershipDate;
}
