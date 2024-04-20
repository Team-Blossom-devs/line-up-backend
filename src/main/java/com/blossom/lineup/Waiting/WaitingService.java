package com.blossom.lineup.Waiting;

import com.blossom.lineup.Waiting.entity.response.CheckWaitingStatus;

public interface WaitingService {

    CheckWaitingStatus myCurrentWaiting(Long waitingId); // 대기현황 조회
}
