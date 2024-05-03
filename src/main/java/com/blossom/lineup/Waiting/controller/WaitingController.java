package com.blossom.lineup.Waiting.controller;

import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.entity.response.CheckWaitingStatus;
import com.blossom.lineup.Waiting.service.WaitingService;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/waiting")
    public Response<Void> create(@Validated @RequestBody WaitingRequest request){
        log.debug("대기 생성");

        waitingService.create(request);
        return Response.ok();
    }

    @DeleteMapping("/waiting/{waitingId}")
    public Response<Void> delete(@PathVariable("waitingId") long waitingId){
        log.debug("대기 삭제");

        waitingService.delete(waitingId);
        return Response.ok();
    }

    // todo : waitingId없이 (대기 걸어두지 않았을 때)의 대기 현황 조회 추가하기.
    @GetMapping("/waiting-status/{waitingId}")
    public Response<CheckWaitingStatus> getCurrentWaitingStatus(@PathVariable("waitingId") long waitingId){
        log.debug("waitingId : "+waitingId);

        return Response.ok(waitingService.myCurrentWaiting(waitingId));
    }
}
