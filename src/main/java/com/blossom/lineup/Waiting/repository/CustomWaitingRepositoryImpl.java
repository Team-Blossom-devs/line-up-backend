package com.blossom.lineup.Waiting.repository;

import java.util.List;

import com.blossom.lineup.Organization.entity.Organization;
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
	public List<Waiting> findWaitingByCursor(Long cursor, Organization organization, int size) {

		BooleanExpression cursorInitiate = cursor == null ? null : qWaiting.id.gt(cursor);

		return query.selectFrom(qWaiting)
			.leftJoin(qWaiting.organization)
			.fetchJoin()
			.leftJoin(qWaiting.customer)
			.fetchJoin()
			.where(cursorInitiate, qWaiting.organization.eq(organization))
			.orderBy(qWaiting.id.asc())
			.limit(size + 1)
			.fetch();
	}
}
