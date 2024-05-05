package com.blossom.lineup.Organization.entity;

import com.blossom.lineup.Member.entity.Manager;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@SQLRestriction(value = "active_status <> 'DELETED'")
@SQLDelete(sql = "UPDATE organization SET active_status = 'DELETED' WHERE organization_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_id")
	private Long id;
	private String name;      	// 주점 이름
	private String introduce; 	// 주점 소개
	private String location;    // 위치
	private Integer tableCount; // 테이블 개수

	@NotNull
	private Integer tableTimeLimit; // 테이블 이용 제한 시간(분)

	@OneToOne
	@JoinColumn(name = "manager_id")
	private Manager manager;

	@OneToMany(mappedBy = "organization")
	private Set<Waiting> waitings;

	@Builder
	public Organization(String name, String introduce, String location, Integer tableCount, Integer tableTimeLimit, Manager manager) {
		this.name = name;
		this.introduce = introduce;
		this.location = location;
		this.tableCount = tableCount;
		this.manager = manager;
		this.waitings = new HashSet<>();
		this.tableTimeLimit = tableTimeLimit;
	}

}
