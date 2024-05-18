package com.blossom.lineup.Organization.entity.response;

import com.blossom.lineup.Organization.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

/**
 * 주점 상세 정보 dto
 */
@Getter
@AllArgsConstructor
public class OrganizationDto {
    private String name;
    private String introduce;
    private String imageUrl;
    private String location;
    private Integer tableCount;
    private String openTime;
    private String closeTime;

    public static OrganizationDto of(Organization o){
        return new OrganizationDto(
                o.getName(),
                o.getIntroduce(),
                o.getImageUrl(),
                o.getLocation(),
                o.getTableCount(),
                o.getOpenTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                o.getCloseTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }
}
