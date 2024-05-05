package com.blossom.lineup.Waiting.repository;

import java.util.List;

import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;

public interface CustomWaitingRepository {

	List<Waiting> findWaitingByCursor(Long cursor, Organization organization, int size);

}
