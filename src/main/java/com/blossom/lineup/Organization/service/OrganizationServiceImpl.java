package com.blossom.lineup.Organization.service;

import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Organization.entity.response.OrganizationSearchList;
import com.blossom.lineup.Organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService{

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationSearchList searchOrganizations(String searchTerm, int pageNum) {
        log.debug("주점 검색 : %"+searchTerm+"% "+pageNum+"p");

        // pageNum은 0페이지 부터 시작, 10개씩 보여줌.
        Pageable pageable = PageRequest.of(pageNum-1,10);
        Page<Organization> organizationPage = organizationRepository
                .findByNameContainingOrLocationContaining(searchTerm,searchTerm,pageable);

        return OrganizationSearchList.of(organizationPage);
    }
}
