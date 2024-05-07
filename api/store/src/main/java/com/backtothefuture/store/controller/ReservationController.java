package com.backtothefuture.store.controller;


import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ReservationRequestDto;
import com.backtothefuture.store.dto.response.MemberDoneReservationResponseDto;
import com.backtothefuture.store.dto.response.MemberProgressReservationResponseDto;
import com.backtothefuture.store.service.ReservationHistoryService;
import com.backtothefuture.store.service.ReservationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
@Tag(name = "예약(주문) API", description = "예약 관련 API 입니다.")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationHistoryService reservationHistoryService;

    @Operation(
            summary = "주문",
            description = "주문 요청 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약(주문) 성공 응답입니다.", useReturnTypeSchema = true,
                            content = @Content(
                                    examples = {
                                            @ExampleObject(name = "주문 번호", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"reservation_id\": 1}}"),

                                    }
                            ))
            })
    @PostMapping
    public ResponseEntity<BfResponse<?>> makeReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ReservationRequestDto dto) throws FirebaseMessagingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("reservation_id", reservationService.makeReservation(userDetails.getId(), dto))));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<BfResponse<?>> getReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("reservationId") Long reservationId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BfResponse<>(SUCCESS, reservationService.getReservation(userDetails, reservationId)));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<BfResponse<?>> cancelReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("reservationId") Long reservationId
    ) {
        reservationService.cancelReservation(userDetails, reservationId);
        return ResponseEntity.status(NO_CONTENT)
                .body(new BfResponse<>(NO_CONTENT));
    }

    @Operation(
            summary = "주문 완료 내역",
            description = "주문 완료 내역 조회 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 완료 내역 조회 성공 응답입니다.", useReturnTypeSchema = true)
            })
    @GetMapping("/done")
    public ResponseEntity<BfResponse<MemberDoneReservationResponseDto>> getMemberDoneReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "마지막으로 조회한 주문 id 값입니다.", required = true) @RequestParam(name = "cursorId", required = true) Long cursorId,
            @Parameter(description = "페이지 크기입니다.", required = true) @RequestParam(name = "size", required = true) Integer size) {
        return ResponseEntity.ok(
                new BfResponse<>(reservationHistoryService.getMemberDoneReservations(userDetails, cursorId, size)));
    }

    @Operation(
            summary = "주문 진행 내역",
            description = "주문 진행 내역 조회 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 진행 내역 조회 성공 응답입니다.", useReturnTypeSchema = true)
            })
    @GetMapping("/proceeding")
    public ResponseEntity<BfResponse<List<MemberProgressReservationResponseDto>>> getMemberProceedingReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                new BfResponse<>(reservationHistoryService.getMemberProceedingReservation(userDetails)));
    }

}
