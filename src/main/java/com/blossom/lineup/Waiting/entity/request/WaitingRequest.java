package com.blossom.lineup.Waiting.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WaitingRequest {
    @NotNull
    private Long organizationId;

    @NotNull
    private Integer headCount;
}
