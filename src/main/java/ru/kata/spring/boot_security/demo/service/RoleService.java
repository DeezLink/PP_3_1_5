package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.Collection;

public interface RoleService {
    Collection<Role> findAll();

    Role getRole(int id);
}
