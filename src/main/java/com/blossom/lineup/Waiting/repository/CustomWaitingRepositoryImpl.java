package com.blossom.lineup.Waiting.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.blossom.lineup.Waiting.entity.QWaiting;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomWaitingRepositoryImpl implements CustomWaitingRepository {

	private final JPAQueryFactory query;
	private final QWaiting qWaiting = QWaiting.waiting;

	@Override
	public List<Waiting> findWaitingByCursor(LocalDateTime cursorTime, Long cursorId, int size) {

		BooleanExpression timeInitiate = cursorTime == null ? null : qWaiting.updatedAt.lt(cursorTime);
		BooleanExpression idInitiate = cursorId == null ? null : qWaiting.id.lt(cursorId);

		return query.selectFrom(qWaiting)
			.where(timeInitiate, idInitiate)
			.orderBy(qWaiting.updatedAt.desc(), qWaiting.id.desc())
			.limit(size + 1)
			.fetch();
	}
}
