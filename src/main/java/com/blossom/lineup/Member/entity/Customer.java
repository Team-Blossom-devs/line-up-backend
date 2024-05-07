package com.blossom.lineup.Member.entity;

import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SQLRestriction(value = "active_status <> 'DELETED'")
@SQLDelete(sql = "UPDATE customer SET active_status = 'DELETED' WHERE customer_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends Member{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Long id;

	private String email;

	private Long socialId;

	@OneToMany(mappedBy = "customer")
	private List<Waiting> waiting = new ArrayList<>();

	@Builder
	public Customer(String userName, String phoneNumber, String email, String refreshToken, Long socialId) {
		super(userName, phoneNumber, Role.USER, refreshToken);
		this.email = email;
		this.socialId = socialId;
	}

	@Override
	public Organization getOrganization() {
		throw new RuntimeException("do not use");
	}

}
