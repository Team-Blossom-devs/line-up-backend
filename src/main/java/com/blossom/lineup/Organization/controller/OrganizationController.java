package com.blossom.lineup.Organization.controller;

import com.blossom.lineup.Organization.entity.response.OrganizationDto;
import com.blossom.lineup.Organization.entity.response.OrganizationSearchList;
import com.blossom.lineup.Organization.service.OrganizationService;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/{organizationId}")
    public Response<OrganizationDto> getOrganization(@PathVariable("organizationId") long organizationId){
        return Response.ok(organizationService.getOrganization(organizationId));
    }

    @GetMapping
    public Response<OrganizationSearchList> searchOrganizations(
            @RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum
    ){
        return Response.ok(organizationService.searchOrganizations(searchTerm,pageNum));
    }
}
