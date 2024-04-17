package com.backtothefuture.store.controller;


import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ReservationRequestDto;
import com.backtothefuture.store.service.ReservationHistoryService;
import com.backtothefuture.store.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationHistoryService reservationHistoryService;

    @PostMapping
    public ResponseEntity<BfResponse<?>> makeReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ReservationRequestDto dto) {
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

    @GetMapping("/done")
    public ResponseEntity<BfResponse<?>> getMemberDoneReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "cursorId", required = true) Long cursorId,
            @RequestParam(name = "size", required = true) Integer size) {
        return ResponseEntity.ok(
                new BfResponse<>(reservationHistoryService.getMemberDoneReservations(userDetails, cursorId, size)));
    }

    @GetMapping("/proceeding")
    public ResponseEntity<BfResponse<?>> getMemberProceedingReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                new BfResponse<>(reservationHistoryService.getMemberProceedingReservation(userDetails)));
    }

}
