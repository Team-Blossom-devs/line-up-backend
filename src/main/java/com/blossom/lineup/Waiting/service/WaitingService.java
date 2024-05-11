package com.blossom.lineup.Waiting.service;

import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.base.Response;

public interface WaitingService {

    void create(WaitingRequest request); // 대기 생성

    Response<?> getWaitingStatus(Long organizationId);

    void delete(Long waitingId); // 대기
}
