package com.blossom.lineup.Waiting.service;

import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.entity.response.CheckWaitingStatus;

public interface WaitingService {

    void create(WaitingRequest request); // 대기 생성

    CheckWaitingStatus myCurrentWaiting(Long waitingId); // 대기현황 조회

    void delete(Long waitingId); // 대기
}
