package com.backtothefuture.security.annotation;


import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.security.service.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDetails principal =
                new UserDetailsImpl(1L, "email@naver.com", "123456",
                        List.of(new SimpleGrantedAuthority(RolesType.ROLE_USER.name())), null);

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(principal, "password", principal.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }
}
