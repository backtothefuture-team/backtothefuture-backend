package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.*;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.service.StoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {
	private final StoreService storeService;

	@PostMapping("/register")
	public ResponseEntity<BfResponse<?>> registerStore(
		@Valid @RequestBody StoreRegisterDto storeRegisterDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new BfResponse<>(CREATE, Map.of("store_id", storeService.registerStore(storeRegisterDto))));
	}
}
