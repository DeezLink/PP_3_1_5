package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    void save();

    List<User> getUsers();

    void save(User user);

    User showUser(int id);

    void editUser(User user);

    void deleteUser(int id);

    Role getRole (Long roleName);

    String decoding(String codePass);
    String[] arrayRoles();
}