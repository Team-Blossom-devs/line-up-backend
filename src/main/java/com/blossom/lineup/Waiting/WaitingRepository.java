package com.blossom.lineup.Waiting;

import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    // 내 앞의 대기 인원
    @Query("SELECT COUNT(w) FROM Waiting w WHERE w.organization = :organization AND w.waitingNumber <= :waitingNumber AND w.entranceStatus = 'WAITING'")
    int countWaitingWithLowerNumber(@Param("organization") Organization organization, @Param("waitingNumber") Integer waitingNumber);
}
