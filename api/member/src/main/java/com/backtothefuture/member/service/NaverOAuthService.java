package com.backtothefuture.member.service;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import org.springframework.stereotype.Service;

@Service("naverOAuthService")
public class NaverOAuthService implements OAuthService {

    @Override
    public String getAccessToken(OAuthLoginDto oAuthLoginDto) {
        return null;
    }

    @Override
    public LoginTokenDto getUserInfoFromResourceServer(OAuthLoginDto OAuthLoginDto) {
        return null;
    }

    @Override
    public Member isMember(Long authId) {
        return null;
    }
}
