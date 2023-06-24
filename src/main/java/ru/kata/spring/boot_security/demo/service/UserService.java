package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    void saveUser(User user);

    User getUser(int id);

    void deleteUser(int id);

    User getUserByName(String s);

    void processUser(User theUser, String[] roles);

    void getListUsersModel(Model theModel, Authentication authentication);
}