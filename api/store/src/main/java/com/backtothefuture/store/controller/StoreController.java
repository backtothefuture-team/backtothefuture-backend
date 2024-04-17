package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.domain.store.FilteringOption;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.dto.response.StoreResponse;
import com.backtothefuture.store.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping("")
    public ResponseEntity<BfResponse<?>> registerStore(
            @Valid @RequestBody StoreRegisterDto storeRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE, Map.of("store_id", storeService.registerStore(storeRegisterDto))));
    }

    @GetMapping("")
    public ResponseEntity<BfResponse<?>> readStores(
            @RequestParam(defaultValue = "distance") String filteringOption,
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        List<StoreResponse> response = storeService.findStores(
                FilteringOption.from(filteringOption),
                cursor,
                size
        );

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, response));
    }
}
