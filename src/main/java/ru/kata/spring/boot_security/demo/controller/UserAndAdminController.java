package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/userAndAdmin")
public class UserAndAdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showUsers(ModelMap model) {
        model.addAttribute("users", userService.getUsers());
        return "adminAndUser";
    }

    @GetMapping(value = "/addUser")
    public String addUser(Model model) {
        User user = new User();
        Role role = new Role();
        model.addAttribute("newUser", user);
        model.addAttribute("roles", user.getRoles());
        model.addAttribute("userRole", role);
        return "newUser";
    }

    @PostMapping(value = "/addUser")
    public String createUser(@ModelAttribute("newUser") User user) {
        User user1 = new User();
        user1.setPassword(user.getPassword());
        user1.setUserName(user.getUserName());
        Set<Role> roles = new HashSet<>();

        for (Role role : user.getRoles()) {
            if (role.getRole().equals("ROLE_ADMIN")) {
                roles.add(userService.getRole(2L));
            }
            if (role.getRole().equals("ROLE_USER")) {
                roles.add(userService.getRole(1L));
            }
        }
        user1.setRole(roles);

        userService.save(user1);
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
        model.addAttribute("ur", new Role());
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
                                 @PathVariable(name = "role_id") Long roleId) {
        Set<Role> userRole = userService.showUser(userId).getRoles();
        userRole = userRole.stream().filter(e -> e.getId() != roleId).collect(Collectors.toSet());
        User user = userService.showUser(userId);
        user.setRole(userRole);
        userService.editUser(user);
        return String.format("redirect:/admin/edit/%d", userId);
    }

    @PatchMapping("/addRole/{user_id}")
    public String addRole(@ModelAttribute("ur") Role role, @PathVariable("user_id") int id) {
        if (role.getRole().equals("ROLE_USER")) {
            User user = userService.showUser(id);
            user.addRole(userService.getRole(1L));
            userService.editUser(user);
        }
        if (role.getRole().equals("ROLE_ADMIN")) {
            User user = userService.showUser(id);
            user.addRole(userService.getRole(2L));
            userService.editUser(user);

        }
        return "redirect:/admin";
    }

}
