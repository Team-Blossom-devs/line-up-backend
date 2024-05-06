package com.blossom.lineup.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {

        String userName = "manager";
        String phoneNumber = "123-456-7890";
        String managerName = "manager";
        String encodedPassword = passwordEncoder.encode("1234");
        UUID uuid = UUID.randomUUID();
        ActiveStatus activeStatus = ActiveStatus.ACTIVATED;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO manager (user_name, phone_number, role, uuid, manager_name, password, active_status) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, userName);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, "MANAGER");
            pstmt.setString(4, uuid.toString());
            pstmt.setString(5, managerName);
            pstmt.setString(6, encodedPassword);
            pstmt.setString(7, String.valueOf(activeStatus));
            pstmt.executeUpdate();
        }
    }
}
