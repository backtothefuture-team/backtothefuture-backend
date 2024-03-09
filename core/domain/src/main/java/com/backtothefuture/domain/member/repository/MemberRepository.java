package com.backtothefuture.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtothefuture.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
	boolean existsByEmail(String email);
	boolean existsByPhoneNumber(String phoneNumber);

	Optional<Member> findByAuthId(String AuthId);
}
