package com.blossom.lineup.Member.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(value = "active_status <> 'DELETED'")
@SQLDelete(sql = "UPDATE manage SET active_status = 'DELETED' WHERE manager_id = ?")
public class Manager extends Member {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "manager_id")
	private Long id;

	//매니저 인증은 managerName과 password로 진행한다.
	@Column(nullable = false)
	private String managerName;

	@Column(nullable = false)
	private String password;

	@OneToOne(mappedBy = "manager")
	private Organization organization;

	@Builder
	public Manager(String userName, String phoneNumber, String refreshToken, Organization organization){
		super(userName, phoneNumber, Role.MANAGER, refreshToken);
		this.organization = organization;
	}

	@Override
	public List<Waiting> getWaiting() {
		throw new RuntimeException("do not use");
	}
}
