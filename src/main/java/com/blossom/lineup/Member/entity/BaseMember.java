package com.blossom.lineup.Member.entity;

import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;

import java.util.List;

public interface BaseMember {
	Long getId();

	Organization getOrganization();

	List<Waiting> getWaiting();
}
