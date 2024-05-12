package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.domain.SortingOption;
import com.backtothefuture.store.dto.request.MemberLocationRequest;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.dto.response.StoreDetailResponse;
import com.backtothefuture.store.dto.response.StoreResponse;
import com.backtothefuture.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Tag(name = "가게 API", description = "가게 관련 API 입니다.")
public class StoreController {
    private final StoreService storeService;

    @PostMapping(
            value = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "새로운 가게 등록",
            description = "가게 정보와 이미지를 등록합니다. 이미지는 'image/png', 'image/jpeg' 형식을 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "등록 성공",
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

    @Operation(
            summary = "가게 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "목록 조회 성공", useReturnTypeSchema = true)
            },
            parameters = {
                    @Parameter(name = "sortingOption", description = "정렬 옵션(값이 없을 시 기본 default 적용)", example = "default/star/distance"),
                    @Parameter(name = "sortingIndex", description = "정렬 옵션이 \"별점\"인 경우 정렬을 위한 옵션(값이 없을 시 0 적용): 이전 페이지의 마지막 가게 sortingIndex", example = "default/star/distance"),
                    @Parameter(name = "cursor", description = "커서(값이 없을 시 기본 0 적용): 이전 페이지의 마지막 가게 식별자", example = "0"),
                    @Parameter(name = "size", description = "페이지 크기(값이 없을 시 기본 10 적용)", example = "10")
            }
    )
    @GetMapping("")
    public ResponseEntity<BfResponse<?>> readStores(
            @RequestParam(defaultValue = "default") String sortingOption,
            @RequestParam(defaultValue = "0") Long sortingIndex,
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestBody(required = false) MemberLocationRequest request
    ) {
        List<StoreResponse> response = storeService.findStores(
                SortingOption.from(sortingOption),
                sortingIndex,
                cursor,
                size,
                page,
                request
        );

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, response));
    }

    @Operation(
            summary = "가게 상세 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "404", description = "조회 실패(존재하지 않는 storeId)", useReturnTypeSchema = true)
            }
    )
    @GetMapping("/{storeId}")
    public ResponseEntity<BfResponse<StoreDetailResponse>> readStore(@PathVariable Long storeId) {
        StoreDetailResponse response = storeService.findStore(storeId);

        return ResponseEntity.ok(
                new BfResponse<>(SUCCESS, response)
        );
    }
}
