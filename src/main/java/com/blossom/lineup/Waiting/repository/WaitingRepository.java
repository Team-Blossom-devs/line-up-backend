package com.blossom.lineup.Waiting.repository;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long>, CustomWaitingRepository {

    // 현재 기다리고 있는 팀이 몇 팀인지 조회
    long countWaitingByEntranceStatus(EntranceStatus status);

    // 내 대기id보다 먼저인 팀이 몇 팀인지 조회
    @Query("SELECT COUNT(w) FROM Waiting w WHERE w.entranceStatus = 'WAITING' AND w.id < :waitingId")
    long countWaitingBeforeMe(@Param("waitingId") Long waitingId);

    // 내가 이전에 organization에 대기를 걸어 놓은적이 있는지 확인.
    Optional<Waiting> findByCustomerAndEntranceStatusIn(Customer customer, Collection<EntranceStatus> statuses);

    // 이용하고 있는 테이블의 모든 Waiting 정보 (updatedAt순으로 정렬)
    @Query("SELECT w FROM Waiting w WHERE w.organization =:organization AND w.entranceStatus = 'COMPLETE' ORDER BY w.updatedAt DESC")
    List<Waiting> findUsingTables(@Param("organization") Organization organization);
}
