package com.blossom.lineup.Waiting;

import com.blossom.lineup.Waiting.entity.response.CheckWaitingStatus;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WaitingController {

    private final WaitingService waitingService;

    // todo : waitingId없이 (대기 걸어두지 않았을 때)의 대기 현황 조회 추가하기.
    @GetMapping("/waiting-status/{waitingId}")
    public Response<CheckWaitingStatus> getCurrentWaitingStatus(@PathVariable("waitingId") Long waitingId){
        log.debug("waitingId : "+waitingId);

        return Response.ok(waitingService.myCurrentWaiting(waitingId));
    }
}
