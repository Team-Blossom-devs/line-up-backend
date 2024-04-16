package com.blossom.lineup.Member.entity;

import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;

public interface BaseMember {
	Long getId();

	Organization getOrganization();

	Waiting getWaiting();
}
