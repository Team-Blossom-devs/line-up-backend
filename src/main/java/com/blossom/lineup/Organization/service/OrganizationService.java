package com.blossom.lineup.Organization.service;

import com.blossom.lineup.Organization.entity.response.OrganizationDto;
import com.blossom.lineup.Organization.entity.response.OrganizationSearchList;

public interface OrganizationService {

    OrganizationDto getOrganization(long organizationId);
    OrganizationSearchList searchOrganizations(String searchTerm, int pageNum);
}
