package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/admin")

public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showUsers(ModelMap model) {
        model.addAttribute("users", userService.getUsers());
        return "adminData";
    }

    @GetMapping(value = "/addUser")
    public String addUser(Model model) {
        User user = new User();
        model.addAttribute("newUser", user);
        return "newUser";
    }

    @PostMapping(value = "/addUser")
    public String createUser(@ModelAttribute("newUser") User user, HttpServletRequest request) {
        Set<Role> roles = new HashSet<>();
        String[] userRoles = request.getParameterValues("role1");

        for (String roleId : userRoles) {
            if (Long.parseLong(roleId) == 2L) {
                roles.add(userService.getRole(2L));
            }
            if (Long.parseLong(roleId) == 1L) {
                roles.add(userService.getRole(1L));
            }
        }
        user.setRole(roles);
        userService.save(user);
        return "redirect:/admin";
    }

    @DeleteMapping(value = "/remove/{id}")
    public String deleteUser(@PathVariable(name = "id") int id) {
        userService.deleteUser(id);
        return "redirect:/admin ";
    }


    @GetMapping(value = "/edit/{id}")
    public String editUser(@PathVariable("id") int id, ModelMap model) {
        model.addAttribute("user", userService.showUser(id));
        model.addAttribute("allRoles", userService.arrayRoles());
        return "editUser";
    }

    @PatchMapping("/edit/{id}")
    public String update(@ModelAttribute("user") User user,
                         @PathVariable("id") int id) {

        Set<Role> role = userService.showUser(id).getRoles();
        user.setRole(role);
        userService.editUser(user);
        return "redirect:/admin ";
    }

    @DeleteMapping(value = "/removeRole/{user_id}/{role_id}")
    public String deleteUserRole(@PathVariable(name = "user_id") int userId,
                                 @PathVariable(name = "role_id") int roleId) {
        Set<Role> userRole = userService.showUser(userId).getRoles();
        userRole = userRole.stream().filter(e -> e.getId() != roleId).collect(Collectors.toSet());
        User user = userService.showUser(userId);
        user.setRole(userRole);
        userService.editUser(user);
        return String.format("redirect:/admin/edit/%d", userId);
    }

    @PatchMapping("/addRole/{user_id}")
    public String addRole(HttpServletRequest request, @PathVariable("user_id") int userId) {
        String[] userRoles = request.getParameterValues("addRole");
        if (userRoles[0].equals("1")) {
            User user = userService.showUser(userId);
            user.addRole(userService.getRole(1L));
            userService.editUser(user);
        } else if (userRoles[0].equals("2")) {
            User user = userService.showUser(userId);
            user.addRole(userService.getRole(2L));
            userService.editUser(user);

        }
        return String.format("redirect:/admin/edit/%d", userId);
    }

}