package com.blossom.lineup.Organization.controller;

import com.blossom.lineup.Organization.entity.response.OrganizationSearchList;
import com.blossom.lineup.Organization.service.OrganizationService;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public Response<OrganizationSearchList> getOrganizations(
            @RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum
    ){
        return Response.ok(organizationService.getOrganizations(searchTerm,pageNum));
    }
}
