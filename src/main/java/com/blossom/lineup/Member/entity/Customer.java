package com.blossom.lineup.Member.entity;

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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends Member{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Long id;

	@OneToOne(mappedBy = "customer")
	private Waiting waiting;

	@Builder
	public Customer(String userName, String phoneNumber, Long socialId, Role role, String email, Waiting waiting){
		super(userName, phoneNumber, socialId, role, email);
		this.waiting = waiting;
	}

	@Override
	public Organization getOrganization() {
		throw new RuntimeException("do not use");
	}

}
