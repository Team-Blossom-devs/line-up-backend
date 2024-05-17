package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.Manager;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m FROM Manager m JOIN FETCH m.organization o WHERE m.managerName = :managerName")
    Optional<Manager> findByManagerName(@Param("managerName") String managerName);

    @Query("SELECT m FROM Manager m JOIN FETCH m.organization o WHERE m.uuid = :uuid")
    Optional<Manager> findByUuid(@Param("uuid") String uuid);

    @EntityGraph(attributePaths = {"organization"})
    @Query("select m from Manager m where m.id=:id")
    Optional<Manager> findWithOrganization(@Param("id") Long id);
}
