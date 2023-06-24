package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);
}