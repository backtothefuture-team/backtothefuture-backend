package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {
    private final StoreService storeService;


    @PostMapping(
            value = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "새로운 가게 등록",
            description = "가게 정보와 선택적 썸네일을 등록합니다. 썸네일은 'image/png', 'image/jpg' 형식을 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "생성 성공",
                            content = @Content(schema = @Schema(implementation = BfResponse.class),
                                    examples = {
                                            @ExampleObject(name = "success", value = "{\"code\": 201, \"message\": \"정상적으로 생성되었습니다.\", \"data\": {\"store_id\": 1}}")}))
            })
    public ResponseEntity<BfResponse<?>> registerStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "요청 정보입니다.")
            @Valid @RequestPart(value = "request") StoreRegisterDto storeRegisterDto,
            @Parameter(description = "가게 이미지로 사용할 이미지를 첨부해 주세요.")
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("store_id", storeService.registerStore(userDetails, storeRegisterDto, thumbnail))));
    }
}
