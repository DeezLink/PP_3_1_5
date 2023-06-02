package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findByUserName(String userName) {
        return (User) entityManager
                .createQuery("SELECT u FROM User u WHERE u.userName=:custName")
                .setParameter("custName", userName)
                .getSingleResult();
    }

    @Override
    public List<User> getUsers() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Transactional
    @Override
    public void save(User user) {
        entityManager.persist(user);
        entityManager.flush();
    }

    @Override
    public User showUser(int id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    @Override
    public void editUser(User user) {
        entityManager.merge(user);
        entityManager.flush();
    }

    @Transactional
    @Override
    public void deleteUser(int id) {
        entityManager.remove(showUser(id));
        entityManager.flush();
    }
}