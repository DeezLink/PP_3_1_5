package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String listCustomers(Model theModel, Authentication authentication) {
        userService.getListUsersModel(theModel, authentication);
        return "list-users";
    }

    @PatchMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") User theUser, @RequestParam("roles") String[] roles) {
        userService.processUser(theUser, roles);
        return "redirect:/admin/list";
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam("userId") int theId) {
        userService.deleteUser(theId);
        return "redirect:/admin/list";
    }
}