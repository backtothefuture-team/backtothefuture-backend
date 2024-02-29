package com.backtothefuture.domain.member;

import com.backtothefuture.domain.common.MutableBaseEntity;
import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.domain.member.enums.StatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = "email"),
	@UniqueConstraint(columnNames = "phoneNumber")
})
public class Member extends MutableBaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String email;			// 이메일

	private String name;			// 이름

	@Setter
	private String password;		// 비밀번호

	@Setter
	private String phoneNumber;		// 연락처

	@Enumerated(EnumType.STRING)
	private StatusType status = StatusType.PENDING; // 상태

	@Enumerated(EnumType.STRING)
	private ProviderType provider;	// 계정정보 공급 서비스

	@Setter
	@Enumerated(EnumType.STRING)
	private RolesType roles = RolesType.ROLE_USER;		// 권한
}
