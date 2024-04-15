package com.blossom.lineup.Organization.entity;

import java.util.HashSet;
import java.util.Set;

import com.blossom.lineup.Member.entity.Manager;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_id")
	private Long id;
	private String name;
	private Integer seatCount;
	private String introduce;

	@OneToOne
	@JoinColumn(name = "manager_id")
	private Manager manager;

	@OneToMany(mappedBy = "organization")
	private Set<Waiting> waitings;

	@Builder
	public Organization(String name, Integer seatCount, String introduce, Manager manager) {
		this.name = name;
		this.seatCount = seatCount;
		this.introduce = introduce;
		this.manager = manager;
		this.waitings = new HashSet<>();
	}

}
