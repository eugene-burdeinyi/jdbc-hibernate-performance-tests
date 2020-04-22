package com.ojdgaf.tests.performance;

import com.ojdgaf.tests.performance.entity.Contract;
import com.ojdgaf.tests.performance.entity.User;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTest {

    private static final String findUsersJdbc = "SELECT " +
            "u.id AS user_id, " +
            "u.firstName AS user_firstName, " +
            "u.lastName AS user_lastName, " +
            "c.id AS contract_id, " +
            "c.contractNo AS contract_number " +
            "FROM user u JOIN contract c ON u.id = c.idUser";

    public static void main(String[] args) {
        Instant before = Instant.now();
        List<User> users = findAllUsersWithContracts();
        Instant after = Instant.now();
        System.out.println("Time in ms = " + Duration.between(before, after).toMillis());
        System.out.println("Users count = " + users.size());
        System.out.println("First user = " + users.get(0));
    }

    private static List<User> findAllUsersWithContracts() {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(Config.JDBC_DRIVER);
            conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(findUsersJdbc);
            Map<Long, User> usersById = new HashMap<>();

            while (rs.next()) {
                Long id = rs.getLong("user_id");
                String firstName = rs.getString("user_firstName");
                String lastName = rs.getString("user_lastName");
                Long contractId = rs.getLong("contract_id");
                String contractNumber = rs.getString("contract_number");

                User user = usersById.get(id);
                if (user == null) {
                    user = new User(id, firstName, lastName);
                    usersById.put(id, user);
                }
                user.addContract(new Contract(contractId, contractNumber));
            }
            rs.close();
            stmt.close();
            conn.close();
            return new ArrayList<>(usersById.values());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }

}
