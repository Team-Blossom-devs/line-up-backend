package com.blossom.lineup.Waiting;

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

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping("/waiting-status/{waitingId}")
    public Response<?> getCurrentWaitingStatus(@PathVariable("waitingId") Long waitingId){
        log.debug("waitingId : "+waitingId);

        return Response.ok(waitingService.myCurrentWaiting(waitingId));
    }
}
