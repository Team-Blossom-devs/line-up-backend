package com.blossom.lineup.Organization.entity.response;

import com.blossom.lineup.Organization.entity.Organization;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationDto {
    private long id;
    private String name;
    private String location;
    private int tableCount;

    public static OrganizationDto of(Organization o){
        return new OrganizationDto(
                o.getId(),
                o.getName(),
                o.getLocation(),
                o.getTableCount()
        );
    }
}
