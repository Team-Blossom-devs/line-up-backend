package com.blossom.lineup.Waiting.service;

import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.entity.response.PendingResponse;
import com.blossom.lineup.Waiting.entity.response.WaitingResponse;
import com.blossom.lineup.base.Response;

public interface WaitingService {

    void create(WaitingRequest request); // 대기 생성

    Response<?> getWaitingStatus(Long organizationId);

    WaitingResponse myCurrentWaiting(Waiting waiting, Long organizationId); // 대기현황 조회

    PendingResponse getPendingStatus(Waiting waiting); // 입장중 상태 조회

    void delete(Long waitingId); // 대기
}
