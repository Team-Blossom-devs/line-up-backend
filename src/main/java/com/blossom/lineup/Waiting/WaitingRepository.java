package com.blossom.lineup.Waiting;

import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    // 나를 포함한 대기 명단
    // (organization에 대기하고 있고(waiting) 내 id보다 작은 id를 가지고 있는 waiting 명단을 id 오름차순으로 정렬)
    List<Waiting> findByOrganizationAndEntranceStatusWaitingAndIdLessThanOrderById(Organization organization, Long id);

    // 이용하고 있는 테이블의 모든 Waiting 정보 (입장시각순으로 정렬)
    List<Waiting> findByOrganizationAndEntranceStatusCompleteOrderByEntranceTime(Organization organization);
}
