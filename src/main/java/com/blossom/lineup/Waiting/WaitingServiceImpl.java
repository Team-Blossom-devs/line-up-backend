package com.blossom.lineup.Waiting;

import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.entity.response.CheckWaitingStatus;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WaitingServiceImpl implements WaitingService{

    private final WaitingRepository waitingRepository;

    private Waiting findWaiting(Long waitingId){
        return waitingRepository.findById(waitingId)
                .orElseThrow(()->new BusinessException(Code.WAITING_NOT_FOUND));
    }

    @Override
    public CheckWaitingStatus myCurrentWaiting(Long waitingId) {
        log.debug("대기 현황 조회 : "+waitingId);

        Waiting waiting = findWaiting(waitingId);
        int currentWaitingCnt = waitingRepository.countWaitingWithLowerNumber(waiting.getOrganization(), waiting.getWaitingNumber());

        return CheckWaitingStatus.of(currentWaitingCnt, waiting);
    }
}
