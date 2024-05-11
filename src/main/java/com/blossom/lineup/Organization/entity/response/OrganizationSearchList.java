package com.blossom.lineup.Organization.entity.response;

import com.blossom.lineup.Organization.entity.Organization;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationSearchList {

    private int currentPage; // 현재 페이지
    private int totalPages; // 모든 페이지 수
    private int currentPageElementCount; // 현재 페이지의 주점 개수
    private List<OrganizationListDto> organizationListDtoList = new ArrayList<>();

    public static OrganizationSearchList of(Page<Organization> organizations){
        return new OrganizationSearchList(
                organizations.getNumberOfElements(),
                organizations.getNumber()+1,
                organizations.getTotalPages(),

                organizations.getContent().stream()
                        .map(OrganizationListDto::of).collect(Collectors.toList())
        );
    }
}
