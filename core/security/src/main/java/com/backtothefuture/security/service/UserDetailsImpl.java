package com.backtothefuture.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.enums.ProviderType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
	private Long id;
	private String email;
	@JsonIgnore
	private String password;
	private List<GrantedAuthority> authorities;
	private ProviderType provider;

	public static UserDetails from(Member member) {
		List<GrantedAuthority> authorities = member.getRoles() != null ?
			List.of(new SimpleGrantedAuthority(member.getRoles().name())): null;

		return new UserDetailsImpl(
			member.getId(),
			member.getEmail(),
			member.getPassword(),
			authorities,
			member.getProvider()
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
