package com.backtothefuture.member.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.BAD_REQUEST;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.DUPLICATED_MEMBER_EMAIL;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.DUPLICATED_MEMBER_PHONE_NUMBER;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.FORBIDDEN_DELETE_MEMBER;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.IMAGE_UPLOAD_FAIL;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_BANK;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_REFRESH_TOKEN;
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
import com.backtothefuture.domain.residence.Residence;
import com.backtothefuture.domain.residence.repository.ResidenceRepository;
import com.backtothefuture.domain.term.Term;
import com.backtothefuture.domain.term.TermHistory;
import com.backtothefuture.domain.term.repository.TermHistoryRepository;
import com.backtothefuture.domain.term.repository.TermRepository;
import com.backtothefuture.member.dto.request.AccountInfoDto;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.MemberUpdateRequestDto;
import com.backtothefuture.member.dto.request.ResidenceInfoDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
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

        // 계좌 정보 업데이트
        Optional.ofNullable(memberUpdateDto.accountInfo()).ifPresent(accountInfo -> {
            updateAccountInfo(member, accountInfo);
        });

        // 거주지 정보 업데이트
        Optional.ofNullable(memberUpdateDto.residenceInfo()).ifPresent(residenceInfo -> {
            updateResidenceInfo(member, residenceInfo);
        });

        memberRepository.save(member);
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
                    .member(member)
                    .build();
        } else {
            if (accountInfo.code() != null) {
                account.updateBankInfo(bank);
            }
            Optional.ofNullable(accountInfo.accountNumber()).ifPresent(account::updateAccountNumber);
        }

        member.updateAccount(account);
    }

    protected void updateResidenceInfo(Member member, ResidenceInfoDto residenceInfo) {
        Residence residence = member.getResidence();
        if (residence == null) {
            residence = Residence.builder()
                    .latitude(residenceInfo.latitude())
                    .longitude(residenceInfo.longitude())
                    .address(residenceInfo.address())
                    .member(member)
                    .build();
        } else {
            residence.updateResidence(residenceInfo.latitude(), residenceInfo.longitude(), residenceInfo.address());
        }
        member.updateResidence(residence);
    }
}
