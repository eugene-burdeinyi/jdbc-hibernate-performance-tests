package com.ojdgaf.tests.performance;

import com.ojdgaf.tests.performance.entity.User;
import org.hibernate.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class HibernateTest {

    private static final String findUsersHql = "SELECT DISTINCT u FROM User u JOIN FETCH u.contracts";

    public static void main(String[] args) {
        Instant before = Instant.now();
        List<User> users = findAllUsersWithContracts();
        HibernateUtil.closeSessionFactory();
        Instant after = Instant.now();
        System.out.println("Time in ms = " + Duration.between(before, after).toMillis());
        System.out.println("Users count = " + users.size());
        System.out.println("First user = " + users.get(0));
    }

    private static List<User> findAllUsersWithContracts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<User> users = session.createQuery(findUsersHql, User.class).getResultList();
        session.getTransaction().commit();
        return users;
    }
}
