package com.example.library.service;

import com.example.library.dto.request.MemberRequestDTO;
import com.example.library.dto.response.MemberResponseDTO;
import com.example.library.entity.Member;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.MemberMapper;
import com.example.library.repository.MemberRepository;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public Page<MemberResponseDTO> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(memberMapper::toResponseDTO);
    }

    public MemberResponseDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        return memberMapper.toResponseDTO(member);
    }

    public List<MemberResponseDTO> searchMembers(String name) {
        return memberRepository.searchByName(name).stream()
                .map(memberMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberResponseDTO createMember(MemberRequestDTO dto) {
        Member member = memberMapper.toEntity(dto);
        return memberMapper.toResponseDTO(memberRepository.save(member));
    }

    @Transactional
    public MemberResponseDTO updateMember(Long id, MemberRequestDTO dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        memberMapper.updateEntityFromDTO(dto, member);
        return memberMapper.toResponseDTO(memberRepository.save(member));
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member", id);
        }
        memberRepository.deleteById(id);
    }
}
