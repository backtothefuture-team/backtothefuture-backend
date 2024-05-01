package com.backtothefuture.member.service;

import com.backtothefuture.domain.term.repository.TermRepository;
import com.backtothefuture.member.dto.response.TermDto;
import com.backtothefuture.member.dto.response.TermListResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermService {
    private final TermRepository termRepository;

    public TermListResponseDto getAllTerms() {
        List<TermDto> terms = termRepository.findAll()
                .stream()
                .map(TermDto::from)
                .collect(Collectors.toList());
        return new TermListResponseDto(terms);
    }
}
