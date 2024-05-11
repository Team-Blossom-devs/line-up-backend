package com.blossom.lineup.Waiting.controller;

import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.service.WaitingService;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping
    public Response<Void> create(@Validated @RequestBody WaitingRequest request){
        log.debug("대기 생성");

        waitingService.create(request);
        return Response.ok();
    }

    @DeleteMapping("/{waitingId}")
    public Response<Void> delete(@PathVariable("waitingId") long waitingId){
        log.debug("대기 삭제");

        waitingService.delete(waitingId);
        return Response.ok();
    }

    @GetMapping("/{organizationId}")
    public Response<?> getCurrentWaitingStatus(@PathVariable("organizationId") long organizationId){
        log.debug("organizationId : "+organizationId);

        return waitingService.getWaitingStatus(organizationId);
    }
}
