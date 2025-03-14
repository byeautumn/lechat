package com.snowyowl.lechat.websocket.user;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void registerUser(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User or username/password cannot be null.");
        }
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists. Please try logging in.");
        }
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setStatus(Status.OFFLINE);
        try {
            repository.save(user);
        } catch (Exception e) {
            logger.error("Error saving user during registration.", e);
            throw new RuntimeException("Failed to register user.", e);
        }
    }

    @Transactional
    public void updateUserStatus(String username, Status status) {
        if (username == null || username.isEmpty() || status == null) {
            throw new IllegalArgumentException("Username and status cannot be null or empty.");
        }
        Optional<User> userOptional = repository.findByUsername(username);
        userOptional.ifPresent(user -> {
            user.setStatus(status);
            try {
                repository.save(user);
            } catch (Exception e) {
                logger.error("Error updating user status.", e);
                throw new RuntimeException("Failed to update user status.", e);
            }
        });
        if (userOptional.isEmpty()) {
            logger.warn("User with username {} not found during status update.", username);
        }
    }

    public List<User> findConnectedUsers() {
        try {
            return repository.findAllByStatus(Status.ONLINE);
        } catch (Exception e) {
            logger.error("Error retrieving connected users.", e);
            throw new RuntimeException("Failed to retrieve connected users.", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        try {
            return repository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error finding user by username.", e);
            throw new RuntimeException("Failed to find user by username.", e);
        }
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Passwords cannot be null or empty.");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}