package com.blossom.lineup.Organization.entity.response;

import com.blossom.lineup.Organization.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrganizationDto {
    private String name;
    private String introduce;
    private String location;
    private Integer tableCount;

    public static OrganizationDto of(Organization o){
        return new OrganizationDto(
                o.getName(),
                o.getIntroduce(),
                o.getLocation(),
                o.getTableCount()
        );
    }
}
