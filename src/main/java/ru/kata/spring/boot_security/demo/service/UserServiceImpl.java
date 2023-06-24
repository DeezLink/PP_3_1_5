package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void saveUser(User theUser) {
        userRepository.saveAndFlush(theUser);
    }

    @Override
    @Transactional
    public void processUser(User theUser, String[] roles) {
        String encode = theUser.getPassword();
        if (theUser.getId() != 0) { // update user
            if (encode.isEmpty()) { // password not changed
                theUser.setPassword(getUser(theUser.getId()).getPassword());
            } else {
                passwordChanged(theUser, encode);
            }
        } else { // new user
            passwordChanged(theUser, encode);
        }

        theUser.getRoles().clear();
        for (String r : roles) {
            theUser.addRole(roleService.getRole(Integer.parseInt(r)));
        }

        saveUser(theUser);
    }

    @Override
    public void getListUsersModel(Model theModel, Authentication authentication) {
        List<User> theUsers = getUsers();
        theModel.addAttribute("users", theUsers);
        User theUser = new User();
        theModel.addAttribute("user", theUser);
        theModel.addAttribute("roles", roleService.findAll());
        String name = authentication.getName();
        User principalUser = getUserByName(name);
        theModel.addAttribute("principal_user", principalUser);
        String roleString = principalUser.getRoles().stream().map(role -> role.getName().replaceAll("ROLE_", ""))
                .collect(Collectors.joining(" "));
        theModel.addAttribute("principal_roles", roleString);
    }

    private void passwordChanged(User theUser, String encode) {
        encode = passwordEncoder.encode(encode);
        theUser.setPassword(encode);
    }

    @Override
    public User getUser(int id) {
        Optional<User> result = userRepository.findById(id);
        User theUser;
        if (result.isPresent()) {
            theUser = result.get();
        } else {
            throw new RuntimeException("Did not find user id - " + id);
        }
        return theUser;
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserByName(String s) {
        return userRepository.findUserByEmail(s);
    }
}