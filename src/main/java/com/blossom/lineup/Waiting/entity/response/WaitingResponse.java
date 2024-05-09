package com.blossom.lineup.Waiting.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaitingResponse { // 대기 현황
    private String waitingStatus; // 대기 상태
    private int currentWaitingNumber; // 현재 내 순서
    private int expectWaitingTime;  // 예상 대기 시간
    private int headCount;            // 인원 수

    public static WaitingResponse of(String waitingStatus, int beforeMeCnt, int expectWaitingTime, int headCount){
        return new WaitingResponse(waitingStatus, beforeMeCnt, expectWaitingTime, headCount);
    }
}
