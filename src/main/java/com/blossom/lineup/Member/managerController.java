package com.blossom.lineup.Member;

import com.blossom.lineup.SecurityUtils;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class managerController {

    private final SecurityUtils securityUtils;

    @PostMapping("/manager")
    public Response<Void> managerOnly() {
        String uuid = securityUtils.getCurrentUserInfo().getUuid();
        log.info("uuid - {}", uuid);
        return Response.ok();
    }
}
