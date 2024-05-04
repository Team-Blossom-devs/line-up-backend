package com.blossom.lineup.Waiting.repository;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long>, CustomWaitingRepository {

    // 내가 이전에 organization에 대기를 걸어 놓은적이 있는지 확인.
    List<Waiting> findByCustomerAndEntranceStatus(Customer customer, EntranceStatus entranceStatus);

    // 나 이전의 대기 명단
    // (organization에 대기하고 있고(waiting) 내 id보다 작은 id를 가지고 있는 waiting 명단을 id 오름차순으로 정렬)
    @Query("SELECT w FROM Waiting w WHERE w.organization =:organization AND w.entranceStatus = 'WAITING' AND w.id = :id ORDER BY w.id")
    List<Waiting> findBeforeMyWaiting(@Param("organization") Organization organization, @Param("id") Long id);

    // 이용하고 있는 테이블의 모든 Waiting 정보 (입장시각순으로 정렬)
    @Query("SELECT w FROM Waiting w WHERE w.organization =:organization AND w.entranceStatus = 'COMPLETE' ORDER BY w.entranceTime")
    List<Waiting> findUsingTables(@Param("organization") Organization organization);
}
