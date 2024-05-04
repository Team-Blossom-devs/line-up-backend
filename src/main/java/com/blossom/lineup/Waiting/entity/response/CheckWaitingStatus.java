package com.blossom.lineup.Waiting.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckWaitingStatus { // 대기 현황
    private int currentWaitingNumber; // 현재 내 순서
    private int expectWaitingTime;  // 예상 대기 시간
    private int headCount;            // 인원 수

    // todo : 예상 대기 시간 계산값 넣기. (현재는 간단한 예상시간 값으로 넣음.)
    public static CheckWaitingStatus of(int beforeMeCnt, int expectWaitingTime, int headCount){
        return new CheckWaitingStatus(beforeMeCnt, expectWaitingTime, headCount);
    }
}
