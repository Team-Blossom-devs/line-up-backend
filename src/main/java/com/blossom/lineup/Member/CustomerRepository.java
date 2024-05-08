package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findBySocialId(Long socialId);

    Optional<Customer> findByUuid(String uuid);

    Optional<Customer> findByEmail(String email);
}
