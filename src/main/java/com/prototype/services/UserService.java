package com.prototype.services;

import com.prototype.entities.User;
import com.prototype.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
    @Transactional
    public User getUserByUserName(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) return null;;
        return user;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public String getRealNameByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        return (user != null) ? user.getRealName() : "";
    }

    public String getAuthorityByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        return (user != null && user.getAuthority() != null) ? user.getAuthority().getAuthority() : null;
    }
    public void encode(User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String enc = passwordEncoder.encode(user.getPassword());
        user.setPassword(enc);
        userRepository.save(user);
    }
}