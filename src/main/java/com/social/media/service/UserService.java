package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.User;
import com.social.media.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("User cannot be blank!");
    }

    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + "not found!"));
    }

    public User update(User updatedUser, String oldPassword) {
        checkValidString(oldPassword, "Password must contain at least one letter!");

        if (updatedUser != null) {
            var oldUser = readById(updatedUser.getId());
            if (passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
                return create(updatedUser);
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password");
        }
        throw new IllegalArgumentException("User cannot be blank!");
    }

    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    public User readByUsername(String username) {
        checkValidString(username, "Username must contains letters in lower case and can contain '-' or '.'");

        return userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User with username " + username + "not found!"));
    }

    public User readByEmail(String email) {
        checkValidString(email, "Email must contains at least one '@' and one '.' symbols");

        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("User with email " + email + "not found!"));
    }

    public Set<User> getAll() {
        return new HashSet<>(userRepository.findAll());
    }

    public List<User> getAllByFirstName(String firstName) {
        checkValidString(firstName, "First name must start with a capital letter and followed by one or more lowercase");

        return userRepository.findAllByFirstName(firstName);
    }

    public List<User> getAllByLastName(String lastName) {
        checkValidString(lastName, "Last name must start with a capital letter and followed by one or more lowercase");

        return userRepository.findAllByLastName(lastName);
    }

    private void checkValidString(String checking, String exception) {
        if (checking == null || checking.trim().isEmpty()) {
            throw new InvalidTextException(exception);
        }
    }
}
