package com.blossom.lineup.Member;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.entity.Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        if (pattern.matcher(username).matches()) {
            log.info("loadCustomerByEmail, managerName - {}", username);
            return loadCustomerByEmail(username);
        }

        log.info("loadManagerByName, email - {}", username);
        return loadManagerByName(username);
    }

    private UserDetails loadCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole().getRole()));
        return CustomUserDetails.userDetailsBuilder()
                .username(customer.getEmail())
                .password(null)
                .authorities(authorities)
                .uuid(customer.getUuid())
                .role(customer.getRole().getRole())
                .build();
    }

    private UserDetails loadManagerByName(String managerName) {
        Manager manager = managerRepository.findByManagerName(managerName)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + manager.getRole().getRole()));
        return CustomUserDetails.userDetailsBuilder()
                .username(manager.getManagerName())
                .password(manager.getPassword())
                .authorities(authorities)
                .uuid(manager.getUuid())
                .role(manager.getRole().getRole())
                .build();
    }
}

