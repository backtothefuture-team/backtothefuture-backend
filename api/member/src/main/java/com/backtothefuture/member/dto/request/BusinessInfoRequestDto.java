package com.backtothefuture.member.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BusinessInfoRequestDto {
    private List<Business> businesses;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Business {
        private BusinessInfoValidateRequestDto validateRequestDto;
    }
}