package com.blossom.lineup.Waiting.entity.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaitingResponse { // 대기 현황
    private String waitingStatus;     // 대기 상태
    private Long waitingId;           // 대기 id
    private Integer currentWaitingNumber; // 현재 내 순서
    private Integer expectWaitingTime;    // 예상 대기 시간
    private Integer headCount;        // 인원 수
}
