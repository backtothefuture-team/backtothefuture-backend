package com.backtothefuture.member.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.BUSINESS_STATUS_ERROR;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.BUSINESS_VALIDATE_ERROR;

import com.backtothefuture.member.dto.request.BusinessInfoRequestDto;
import com.backtothefuture.member.dto.request.BusinessInfoValidateRequestDto;
import com.backtothefuture.member.dto.response.BusinessStatusResponseDto;
import com.backtothefuture.member.dto.response.BusinessValidationResponseDto;
import com.backtothefuture.member.exception.MemberException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class MemberBusinessService {

    @Value("${validate.business.api_key}")
    private String businessValidateApiKey;

    @Value("${validate.business.baseurl}")
    private String businessValidateBaseUrl;

    // WebClient 인스턴스 생성
    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(businessValidateBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public boolean validateBusinessInfo(BusinessInfoValidateRequestDto validateRequestDto) {

        // 사업자등록정보 진위확인 API 요청정보 생성
        URI uri = UriComponentsBuilder
                .fromHttpUrl(businessValidateBaseUrl + "/validate")
                .queryParam("serviceKey", businessValidateApiKey)
                .build(true).toUri(); // encoded:true -> 이중 인코딩 방지

        BusinessInfoRequestDto.Business business = new BusinessInfoRequestDto.Business(validateRequestDto);

        // 요청 body 설정
        BusinessInfoRequestDto requestBody = new BusinessInfoRequestDto(List.of(business));

        //사업자등록정보 진위확인 API 요청
        BusinessValidationResponseDto response = getWebClient().post()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    throw new MemberException(BUSINESS_VALIDATE_ERROR);
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    throw new MemberException(BUSINESS_VALIDATE_ERROR);
                })
                .bodyToMono(BusinessValidationResponseDto.class)
                .block(); // 동기적

        /**
         * 결과의 valid 값을 조회
         * 01: 일치할경우
         * 02: 일치하지 않을 경우
         */
        if (response != null && response.data() != null && !response.data().isEmpty()) {
            return response.data().get(0).valid().equals("01");
        } else {
            // 알 수 없는 요인에 의해 실패한 요청
            throw new MemberException(BUSINESS_VALIDATE_ERROR);
        }
    }

    public boolean validateBusinessNumber(String businessNumber) {
        // 사업자등록정보 진위확인 API 요청정보 생성
        URI uri = UriComponentsBuilder
                .fromHttpUrl(businessValidateBaseUrl + "/status")
                .queryParam("serviceKey", businessValidateApiKey)
                .build(true).toUri(); // encoded:true -> 이중 인코딩 방지

        //사업자등록정보 진위확인 API 요청
        BusinessStatusResponseDto response = getWebClient().post()
                .uri(uri)
                .bodyValue(Map.of("b_no", List.of(businessNumber)))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    throw new MemberException(BUSINESS_STATUS_ERROR);
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error(clientResponse.);
                    throw new MemberException(BUSINESS_STATUS_ERROR);
                })
                .bodyToMono(BusinessStatusResponseDto.class)
                .block(); // 동기적

        /**
         * 결과의 status 값을 조회
         * 01: 계속사업자
         * 02: 휴업자
         * 03: 폐업자
         */
        if (response != null && response.data() != null && !response.data().isEmpty()) {
            String businessStatus = response.data().get(0).businessStatus(); // 명칭
            String businessStatusCode = response.data().get(0).businessStatusCode(); // 코드

            log.info(String.format("%s business number status code=%s, name=%s",
                    businessNumber, businessStatusCode, businessStatus));

            return businessStatusCode.equals("01");
        } else {
            // 알 수 없는 요인에 의해 실패한 요청
            throw new MemberException(BUSINESS_STATUS_ERROR);
        }
    }
}
