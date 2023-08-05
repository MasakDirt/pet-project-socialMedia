package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Role;
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

    public User create(User user, Role role) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(role);
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("User cannot be blank!");
    }

    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " not found!"));
    }

    public User readByIdOrUsernameOrEmail(long id, String username, String email) {
        return userRepository.findByIdOrUsernameOrEmail(id, username, email)
                .orElseThrow(() -> new EntityNotFoundException(
                                String.format("User with id %d not found/ User with username %s not found/ User with email %s not found",
                                        id, username, email)
                        )
                );
    }

    public User update(User updatedUser, String oldPassword) {
        checkValidString(oldPassword, "Password must contain at least one letter!");

        if (updatedUser != null) {
            return getUser(updatedUser, oldPassword);
        }
        throw new IllegalArgumentException("User cannot be blank!");
    }

    public User updateNamesById(long id, String firstName, String lastName) {
        checkValidString(firstName, "First name must start with a capital letter and followed by one or more lowercase");
        checkValidString(lastName, "Last name must start with a capital letter and followed by one or more lowercase");

        var user = readById(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    public User updateNamesByUsernameOrEmail(String currentUsernameOrEmail, String firstName, String lastName) {
        checkValidString(firstName, "First name must start with a capital letter and followed by one or more lowercase");
        checkValidString(lastName, "Last name must start with a capital letter and followed by one or more lowercase");

        var user = getUserByUsernameOrEmail(currentUsernameOrEmail);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    public User updatePasswordById(long id, String oldPassword, String newPassword) {
        checkValidString(oldPassword, "The 'password' cannot be blank!");
        checkValidString(newPassword, "The 'password' cannot be blank!");
        var oldUser = readById(id);

        return userForUpdatePasswords(oldUser, oldPassword, newPassword);
    }

    public User updatePasswordByUsernameOrEmail(String currentUsernameOrEmail, String oldPassword, String newPassword) {
        checkValidString(oldPassword, "The 'password' cannot be blank!");
        checkValidString(newPassword, "The 'password' cannot be blank!");

        var oldUser = getUserByUsernameOrEmail(currentUsernameOrEmail);

        return userForUpdatePasswords(oldUser, oldPassword, newPassword);
    }

    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    public void delete(String currentUsernameEmail) {
        userRepository.delete(getUserByUsernameOrEmail(currentUsernameEmail));
    }

    public void delete(long id, String username, String email) {
        userRepository.delete(readByIdOrUsernameOrEmail(id, username, email));
    }

    public User readByUsername(String username) {
        checkValidString(username, "Username must contains letters in lower case and can contain '-' or '.'");

        return userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User with username " + username + " not found!"));
    }

    public User readByEmail(String email) {
        checkValidString(email, "Email must contains at least one '@' and one '.' symbols");

        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("User with email " + email + " not found!"));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllByFirstName(String firstName) {
        checkValidString(firstName, "First name must start with a capital letter and followed by one or more lowercase");

        return userRepository.findAllByFirstName(firstName);
    }

    public List<User> getAllByLastName(String lastName) {
        checkValidString(lastName, "Last name must start with a capital letter and followed by one or more lowercase");

        return userRepository.findAllByLastName(lastName);
    }

    public User getUserByUsernameOrEmail(String currentUsernameEmail) {
        checkValidString(currentUsernameEmail, "Username must contains letters in lower case and can contain '-' or '.' /" +
                " Email must contains at least one '@' and one '.' symbols");

        return userRepository.findByUsernameOrEmail(currentUsernameEmail, currentUsernameEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with username or email '" + currentUsernameEmail + "' not found!"));
    }

    public List<User> getAllByRole(String roleName) {
        checkValidString(roleName, "Role name cannot be empty, please write a name!");

        return userRepository.findAllByRoleName(roleName);
    }

    private User getUser(User updatedUser, String oldPassword) {
        var oldUser = readByIdOrUsernameOrEmail(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail());

        if (passwordEncoder.matches(oldPassword, oldUser.getPassword())) {

            updatedUser.setId(oldUser.getId());
            updatedUser.setEmail(oldUser.getEmail());
            updatedUser.setUsername(oldUser.getUsername());

            return create(updatedUser, oldUser.getRole());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password");
    }

    private User userForUpdatePasswords(User oldUser, String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            oldUser.setPassword(newPassword);
            return create(oldUser, oldUser.getRole());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password");
    }

    private void checkValidString(String checking, String exception) {
        if (checking == null || checking.trim().isEmpty()) {
            throw new InvalidTextException(exception);
        }
    }
}
