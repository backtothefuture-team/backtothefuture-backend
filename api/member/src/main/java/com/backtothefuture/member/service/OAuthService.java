package com.backtothefuture.member.service;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;

public interface OAuthService {

    String getAccessToken(OAuthLoginDto oAuthLoginDto);

    LoginTokenDto getUserInfoFromResourceServer(OAuthLoginDto OAuthLoginDto);

    Member isMember(Long authId);
}
