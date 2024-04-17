package com.blossom.lineup.Waiting.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.blossom.lineup.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@SQLRestriction(value = "active_status <> 'DELETED'")
@SQLDelete(sql = "UPDATE waiting SET active_status = 'DELETED' WHERE waiting_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waiting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "waiting_id")
	private Long id;

	private Integer headCount;
	private LocalDateTime entranceTime;
	private Integer tableNumber;
	private Integer waitingNumber;
	@Enumerated(value = EnumType.STRING)
	private EntranceStatus entranceStatus;

	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Builder
	public Waiting(Integer headCount, LocalDateTime entranceTime, Integer tableNumber, Integer waitingNumber,
		Organization organization, Customer customer, EntranceStatus entranceStatus){
		this.headCount = headCount;
		this.entranceTime = entranceTime;
		this.tableNumber = tableNumber;
		this.waitingNumber = waitingNumber;
		this.organization = organization;
		this.customer = customer;
		this.entranceStatus = entranceStatus;
	}

}
