package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.entity.Manager;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.SecurityUtils;
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
public class MemberService {

    private final SecurityUtils securityUtils;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    public void logout() {
        String logoutToken = "logout";

        String uuid = securityUtils.getCurrentUserInfo().getUuid();
        String role = securityUtils.getCurrentUserInfo().getRole();

        if (role.equals(Role.USER.getRole())) {
            Customer customer = customerRepository.findByUuid(uuid).orElseThrow(() -> new BusinessException(Code.CUSTOMER_NOT_FOUND));
            customer.updateRefreshToken(logoutToken);
        }
        else {
            Manager manager = managerRepository.findByUuid(uuid).orElseThrow(() -> new BusinessException(Code.CUSTOMER_NOT_FOUND));
            manager.updateRefreshToken(logoutToken);
        }

    }
}
