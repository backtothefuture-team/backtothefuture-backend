package com.backtothefuture.member.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.BAD_REQUEST;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.DUPLICATED_MEMBER_EMAIL;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.DUPLICATED_MEMBER_PHONE_NUMBER;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.FORBIDDEN_DELETE_MEMBER;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.FORBIDDEN_RESET_PASSWORD;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.IMAGE_UPLOAD_FAIL;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_BANK;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_REFRESH_TOKEN;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_TERM_HISTORY;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_MATCH_REFRESH_TOKEN;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.PASSWORD_NOT_MATCHED;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.REQUIRED_TERM_ACCEPT;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.UNSUPPORTED_IMAGE_EXTENSION;

import com.backtothefuture.domain.account.Account;
import com.backtothefuture.domain.account.repository.AccountRepository;
import com.backtothefuture.domain.bank.Bank;
import com.backtothefuture.domain.bank.repository.BankRepository;
import com.backtothefuture.domain.common.repository.RedisRepository;
import com.backtothefuture.domain.common.util.ConvertUtil;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.residence.repository.ResidenceRepository;
import com.backtothefuture.domain.term.Term;
import com.backtothefuture.domain.term.TermHistory;
import com.backtothefuture.domain.term.repository.TermHistoryRepository;
import com.backtothefuture.domain.term.repository.TermRepository;
import com.backtothefuture.member.dto.request.AccountInfoDto;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberPasswordResetRequestDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.MemberUpdateRequestDto;
import com.backtothefuture.member.dto.request.RegistrationTokenRequestDto;
import com.backtothefuture.member.dto.request.TermHistoryUpdateDto;
import com.backtothefuture.member.dto.response.AccountResponseInfoDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.dto.response.MemberInfoDto;
import com.backtothefuture.member.exception.MemberException;
import com.backtothefuture.security.jwt.JwtProvider;
import com.backtothefuture.security.service.UserDetailsImpl;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TermHistoryRepository termHistoryRepository;
    private final TermRepository termRepository;
    private final ResidenceRepository residenceRepository;
    private final BankRepository bankRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisRepository redisRepository;
    private final S3Util s3Util;

    /**
     * 로그인, OAuth 신규 회원 로그인
     */
    public LoginTokenDto login(MemberLoginDto memberloginDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberloginDto.getEmail(),
                memberloginDto.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetail = (UserDetailsImpl) authenticated.getPrincipal();

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = new LoginTokenDto(accessToken, refreshToken);

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }

    /**
     * 회원 등록
     */
    @Transactional
    public Long registerMember(MemberRegisterDto memberRegisterDto, MultipartFile thumbnail) {
        // 아이디 중복 체크
        if (memberRepository.existsByEmail(memberRegisterDto.getEmail())) {
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        } else if (memberRepository.existsByPhoneNumber(memberRegisterDto.makePhoneNumberString())) {
            throw new MemberException(DUPLICATED_MEMBER_PHONE_NUMBER);
        }

        // 필수 동의 약관 체크
        List<Term> allTerms = termRepository.findAll();
        checkRequiredTerms(allTerms, memberRegisterDto.getAccpetedTerms());

        // 비밀번호 확인
        validatePassword(memberRegisterDto.getPassword(), memberRegisterDto.getPasswordConfirm());

        Member member = ConvertUtil.toDtoOrEntity(memberRegisterDto, Member.class);
        member.setPassword(passwordEncoder.encode(memberRegisterDto.getPassword()));
        member.setPhoneNumber(memberRegisterDto.makePhoneNumberString());

        Long id = memberRepository.save(member).getId();

        // 약관 동의 여부 저장
        allTerms.forEach(term -> {
            TermHistory termHistory = TermHistory.builder()
                    .member(member)
                    .term(term)
                    .isAccepted(memberRegisterDto.getAccpetedTerms().contains(term.getId()))
                    .build();
            termHistoryRepository.save(termHistory);
        });

        // 이미지 업로드
        if (thumbnail != null && !thumbnail.isEmpty()) {
            try {
                String imageUrl = s3Util.uploadMemberProfile(String.valueOf(id), thumbnail);
                member.setProfileUrl(imageUrl);
            } catch (IllegalArgumentException e) {
                throw new MemberException(UNSUPPORTED_IMAGE_EXTENSION);
            } catch (IOException e) {
                throw new MemberException(IMAGE_UPLOAD_FAIL);
            }
        }

        // 모든 회원 가입 절차 종료 후 회원을 active 상태로 변경
        member.activeMember();

        return id;
    }

    /**
     * password, passwordConfirm 체크
     */
    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MemberException(PASSWORD_NOT_MATCHED);
        }
    }

    /**
     * OAuth 기존 회원 로그인
     */
    @Transactional
    public LoginTokenDto OAuthLogin(Member member) {

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }

    /**
     * 기존 refresh token으로 신규 access token, refresh token 발급
     */
    @Transactional
    public LoginTokenDto refreshToken(String oldRefreshToken, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // redis 갱신된 refresh token 유효성 검증
        if (!redisRepository.hasKey(member.getId())) {
            throw new MemberException(NOT_FOUND_REFRESH_TOKEN);
        }

        // redis에 저장된 토큰과 비교
        if (!redisRepository.getRefreshToken(member.getId()).get("refreshToken").equals(oldRefreshToken)) {
            throw new MemberException(NOT_MATCH_REFRESH_TOKEN);
        }

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String newRefreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), newRefreshToken);

        return loginTokenDto;

    }

    /**
     * 회원 기본 정보 조회
     */
    public MemberInfoDto getMemberInfo(Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 계좌 정보 설정
        AccountResponseInfoDto accountInfo = Optional.ofNullable(member.getAccount())
                .map(account -> AccountResponseInfoDto.builder()
                        .code(account.getBank().getCode())
                        .name(account.getBank().getName())
                        .accountHolder(account.getAccountHolder())
                        .accountNumber(account.getAccountNumber())
                        .build())
                .orElse(null);

        return MemberInfoDto.builder()
                .id(member.getId())
                .authId(member.getAuthId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .profile(member.getProfile())
                .birth(member.getBirth())
                .gender(member.getGender())
                .accountInfo(accountInfo)
                .build();
    }

    /**
     * 회원 기본 정보 수정
     */
    @Transactional
    public void updateMemberInfo(UserDetailsImpl userDetails, Long memberId, MemberUpdateRequestDto memberUpdateDto) {
        // 현재 로그인된 유저와 수정하려는 회원이 일치하는지 확인
        if (!userDetails.getId().equals(memberId)) {
            throw new MemberException(BAD_REQUEST);
        }

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 닉네임 업데이트
        Optional.ofNullable(memberUpdateDto.name()).ifPresent(member::updateName);

        // 핸드폰 전화번호 업데이트
        Optional.ofNullable(memberUpdateDto.phoneNumber()).map(phones -> String.join("-", phones))
                .ifPresent(member::updatePhoneNumber);

        // 생년월일 업데이트
        Optional.ofNullable(memberUpdateDto.birth()).ifPresent(member::updateBirth);

        // 성별 업데이트
        Optional.ofNullable(memberUpdateDto.gender()).ifPresent(member::updateGender);

        // 계좌 정보 업데이트
        Optional.ofNullable(memberUpdateDto.accountInfo()).ifPresent(accountInfo -> {
            updateAccountInfo(member, accountInfo);
        });

        memberRepository.save(member);
    }

    /**
     * 약관 동의 업데이트
     */
    @Transactional
    public void updateTermAgreement(Long memberId, Long termId, TermHistoryUpdateDto termHistoryUpdateDto) {
        TermHistory termHistory = termHistoryRepository.findByTermIdAndMemberId(termId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_TERM_HISTORY));

        // 약관 동의 철회일때, 필수 약관 체크
        if (!termHistoryUpdateDto.isAccepted()) {
            Term term = termHistory.getTerm();
            if (term.isRequired()) {
                throw new MemberException(REQUIRED_TERM_ACCEPT);
            }
        }

        termHistory.updateIsAccepted(termHistoryUpdateDto.isAccepted());
    }

    /**
     * 회원 탈퇴 실제 삭제처리 하지 않고, INACTIVE 처리
     */
    @Transactional
    public void inactiveMember(UserDetailsImpl userDetails, Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 현재 로그인된 유저와 탈퇴하려는 회원이 일치하는지 확인 (관리자는 예외)
        if (member.getRoles() != RolesType.ROLE_ADMIN
                && !userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_DELETE_MEMBER);
        }

        member.inactiveMember();
    }

    protected void checkRequiredTerms(List<Term> allTerms, List<Long> acceptedTerms) {
        allTerms.stream().filter(Term::isRequired).forEach(term -> {
            if (!acceptedTerms.contains(term.getId())) {
                throw new MemberException(REQUIRED_TERM_ACCEPT);
            }
        });
    }

    protected void updateAccountInfo(Member member, AccountInfoDto accountInfo) {
        Account account = member.getAccount();
        Bank bank = bankRepository.findByCode(accountInfo.code())
                .orElseThrow(() -> new MemberException(NOT_FOUND_BANK));

        if (account == null) {
            account = Account.builder()
                    .bank(bank)
                    .accountNumber(accountInfo.accountNumber())
                    .accountHolder(accountInfo.accountHolder())
                    .member(member)
                    .build();
        } else {
            if (accountInfo.code() != null) {
                account.updateBankInfo(bank);
            }
            Optional.ofNullable(accountInfo.accountNumber()).ifPresent(account::updateAccountNumber);
            Optional.ofNullable(accountInfo.accountHolder()).ifPresent(account::updateAccountHolder);
        }

        member.updateAccount(account);
    }

    /**
     * 사용자 기기 등록 토큰 저장
     */
    @Transactional
    public void saveRegistrationToken(UserDetailsImpl userDetail, RegistrationTokenRequestDto dto) {
        Member member = memberRepository.findById(userDetail.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));
        member.setRegistrationToken(dto.registrationToken());
    }

    /**
     * 비밀번호 재발급
     */
    @Transactional
    public void resetPassword(UserDetailsImpl userDetails, Long memberId,
                              MemberPasswordResetRequestDto resetRequestDto) {
        // 본인 or 관리자 권한 확인
        if (!userDetails.getAuthorities().contains(RolesType.ROLE_ADMIN)
                && !userDetails.getId().equals(memberId)) {
            throw new MemberException(FORBIDDEN_RESET_PASSWORD);
        }

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // 비밀번호 확인란 검증
        if (!resetRequestDto.newPassword().equals(resetRequestDto.newPasswordConfirm())) {
            throw new MemberException(PASSWORD_NOT_MATCHED);
        }

        member.resetPassword(passwordEncoder.encode(resetRequestDto.newPassword()));
    }
}
