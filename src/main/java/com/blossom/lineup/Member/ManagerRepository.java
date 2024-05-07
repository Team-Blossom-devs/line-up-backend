package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByManagerName(String managerName);

    Optional<Manager> findByUuid(String Uuid);
}
