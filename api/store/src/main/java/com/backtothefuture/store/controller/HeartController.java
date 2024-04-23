package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.store.service.HeartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stores/{storeId}/likes")
@Tag(name = "찜 API", description = "찜 관련 API 입니다.")
public class HeartController {

    private final HeartService heartService;

    public HeartController(HeartService heartService) {
        this.heartService = heartService;
    }

    @Operation(summary = "상점 찜 등록", description = "특정 상점에 대해 특정 사용자가 찜을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "찜 추가 성공", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = BfResponse.class),
            examples = {
                    @ExampleObject(name = "success", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": \"SUCCESS\"}")
            }))
    @PostMapping("/{memberId}")
    public ResponseEntity<BfResponse<?>> addLike(@PathVariable Long storeId, @PathVariable Long memberId) {
        heartService.addHeart(memberId, storeId);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    @Operation(summary = "상점 찜 취소", description = "특정 상점에 대해 특정 사용자의 찜을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "찜 취소 성공", content = @Content(schema = @Schema(hidden = true)))
    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> removeLike(@PathVariable Long storeId, @PathVariable Long memberId) {
        heartService.removeHeart(memberId, storeId);
        return ResponseEntity.noContent().build();
    }
}
