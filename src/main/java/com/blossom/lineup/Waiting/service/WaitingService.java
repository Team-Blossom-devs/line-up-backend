package com.blossom.lineup.Waiting.service;

import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.base.Response;
import org.springframework.core.io.Resource;

public interface WaitingService {

    void create(WaitingRequest request); // 대기 생성

    Response<?> getWaitingStatus(Long organizationId); // 대기 현황 조회(waiting, pending)

    Resource getQrCodeAsMultipartFile(Long waitingId); // pending 상태일 때, qr-code 가져오기

    void delete(Long waitingId); // 대기
}