package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.entity.dto.CustomerSignUpRequest;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.SecurityUtils;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final SecurityUtils securityUtils;
    private final CustomerRepository customerRepository;

    //카카오 회원가입 후 추가 정보 등록 메서드
    public void oauthSignUp(CustomerSignUpRequest request) {

        CustomUserDetails userInfo = securityUtils.getCurrentUserInfo();
        String uuid = userInfo.getUuid();

        Customer customer = customerRepository.findByUuid(uuid).orElseThrow(() -> new BusinessException(Code.CUSTOMER_NOT_FOUND));

        customer.updateUserName(request.getUserName());
        customer.updatePhoneNumber(request.getPhoneNumber());
        customer.updateUpdateRole(Role.USER);
    }
}
