package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.*;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/login")
	public ResponseEntity<BfResponse<?>> login(
		@Valid @RequestBody MemberLoginDto memberLoginDto) {
		return ResponseEntity.ok(new BfResponse<>(memberService.login(memberLoginDto)));
	}

	@PostMapping("/register")
	public ResponseEntity<BfResponse<?>> registerMember(
		@Valid @RequestBody MemberRegisterDto reqMemberDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new BfResponse<>(CREATE, Map.of("id", memberService.registerMember(reqMemberDto))));
	}

}
