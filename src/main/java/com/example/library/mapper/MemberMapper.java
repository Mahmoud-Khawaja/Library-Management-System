package com.example.library.mapper;

import com.example.library.dto.request.MemberRequestDTO;
import com.example.library.dto.response.MemberResponseDTO;
import com.example.library.entity.Member;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponseDTO toResponseDTO(Member member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membershipDate", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    Member toEntity(MemberRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membershipDate", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    void updateEntityFromDTO(MemberRequestDTO dto, @MappingTarget Member member);
}
