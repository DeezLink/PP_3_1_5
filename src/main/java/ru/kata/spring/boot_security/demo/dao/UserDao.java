package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {

    User findByUserName(String userName);

    List<User> getUsers();

    void save(User user);

    User showUser(int id);

    void editUser(User user);

    void deleteUser(int id);
}