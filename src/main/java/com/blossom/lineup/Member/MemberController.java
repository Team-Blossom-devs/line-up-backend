package com.blossom.lineup.Member;

import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/logout")
    public Response<Void> logout() {
        memberService.logout();
        return Response.ok();
    }
}
