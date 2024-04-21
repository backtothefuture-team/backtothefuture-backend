package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.service.HeartService;
import com.backtothefuture.store.service.StoreService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("")
    public ResponseEntity<BfResponse<?>> registerStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart(value = "request") StoreRegisterDto storeRegisterDto,
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("store_id", storeService.registerStore(userDetails, storeRegisterDto, thumbnail))));
    }
}
