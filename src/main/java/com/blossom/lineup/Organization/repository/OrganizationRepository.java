package com.blossom.lineup.Organization.repository;

import com.blossom.lineup.Organization.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // 주점 이름 혹은 위치에 검색어가 들어있으면 검색됨.
    Page<Organization> findByNameContainingOrLocationContaining(String name, String loc, Pageable pageable);
}
