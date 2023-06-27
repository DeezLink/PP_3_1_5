package ru.kata.spring.boot_security.demo.rest;


import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userAPI")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/getCurrentUser")
    public ResponseEntity<User> getUser(Authentication authentication) {
        User user = new User();

        if (authentication != null) {
            String name = authentication.getName();
            user = userService.getUserByName(name);
        } else {
            user.setEmail("not authorized!");
        }
        return ResponseEntity.ok(user);
    }
}