package com.blossom.lineup.Waiting.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.blossom.lineup.Waiting.entity.Waiting;

public interface CustomWaitingRepository {

	List<Waiting> findWaitingByCursor(LocalDateTime cursorTime, Long cursorId, int size);

}
