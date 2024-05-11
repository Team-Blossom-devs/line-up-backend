package com.blossom.lineup.Organization.entity.response;

import com.blossom.lineup.Organization.entity.Organization;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationListDto {
    private long id;
    private String name;
    private String location;
    private int tableCount;

    public static OrganizationListDto of(Organization o){
        return new OrganizationListDto(
                o.getId(),
                o.getName(),
                o.getLocation(),
                o.getTableCount()
        );
    }
}
