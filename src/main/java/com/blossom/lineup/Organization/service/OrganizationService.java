package com.blossom.lineup.Organization.service;

import com.blossom.lineup.Organization.entity.response.OrganizationSearchList;

public interface OrganizationService {
    OrganizationSearchList getOrganizations(String searchTerm, int pageNum);
}
