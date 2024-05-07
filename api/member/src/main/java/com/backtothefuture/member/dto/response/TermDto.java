package com.backtothefuture.member.dto.response;

import com.backtothefuture.domain.term.Term;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TermDto(

        @Schema(description = "약관 ID")
        Long id,

        @Schema(description = "약관 제목")
        String title,

        @Schema(description = "약관 내용")
        String content,

        @Schema(description = "필수 여부")
        boolean isRequired
) {
    public static TermDto from(Term term) {
        return TermDto.builder()
                .id(term.getId())
                .title(term.getTitle())
                .content(term.getContent())
                .isRequired(term.isRequired())
                .build();
    }
}
