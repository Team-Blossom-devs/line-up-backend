package com.blossom.lineup.Organization.entity.response;

import com.blossom.lineup.Organization.entity.Organization;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 주점 검색 페이지에서 보여주는 간략한 주점 dto
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationListDto {
    private long id;
    private String name;
    private String imageUrl;
    private String location;
    private int tableCount;

    public static OrganizationListDto of(Organization o){
        return new OrganizationListDto(
                o.getId(),
                o.getName(),
                o.getImageUrl(),
                o.getLocation(),
                o.getTableCount()
        );
    }
}
