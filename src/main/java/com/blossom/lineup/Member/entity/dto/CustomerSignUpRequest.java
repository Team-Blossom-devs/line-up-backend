package com.blossom.lineup.Member.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSignUpRequest {

    @NotNull
    private String userName;

    @NotNull
    private String phoneNumber;
}
