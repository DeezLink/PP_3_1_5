package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private void passwordChanged(User theUser, String encode) {
        encode = passwordEncoder.encode(encode);
        theUser.setPassword(encode);
    }

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
    public void processUser(User theUser) {
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
        saveUser(theUser);
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