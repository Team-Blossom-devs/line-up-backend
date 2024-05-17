package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.dto.SignUpRequest;
import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/oauth/sign-up")
    public Response<Void> oauthSignUp(@Validated @RequestBody SignUpRequest signUpRequest) {

        customerService.oauthSignUp(signUpRequest);
        return Response.ok();
    }
}
