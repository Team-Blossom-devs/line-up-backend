package com.blossom.lineup.Waiting.entity;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.blossom.lineup.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@SQLRestriction(value = "active_status <> 'DELETED'")
@SQLDelete(sql = "UPDATE waiting SET active_status = 'DELETED' WHERE waiting_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table
public class Waiting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "waiting_id")
	private Long id;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private EntranceStatus entranceStatus;	// 입장 상태

	@NotNull
	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;		// 주점

	@NotNull
	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;				// 대기자

	private Integer headCount; 				// 인원수
	private LocalDateTime entranceTime;     // 입장 시간
	private Integer tableNumber;            // 배정받은 테이블 번호

	@Builder
	public Waiting(Integer headCount, LocalDateTime entranceTime, Integer tableNumber,Organization organization,
				   Customer customer, EntranceStatus entranceStatus){
		this.headCount = headCount;
		this.entranceTime = entranceTime;
		this.tableNumber = tableNumber;
		this.organization = organization;
		this.customer = customer;
		this.entranceStatus = entranceStatus;
	}
}
