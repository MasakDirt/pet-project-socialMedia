package com.social.media.service;

import com.social.media.model.entity.User;
import com.social.media.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(@NotNull User user) {
        return userRepository.save(user);
    }

    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + "not found!"));
    }

    public User update(@NotNull User updatedUser) {
        readById(updatedUser.getId());
        return userRepository.save(updatedUser);
    }

    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    public User readByUsername(
            @NotNull @NotBlank(message = "Username cannot be blank")
            String username
    ) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User with username " + username + "not found!"));
    }

    public User readByEmail(
            @NotNull @NotBlank(message = "Email cannot be blank")
            String email
    ) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("User with email " + email + "not found!"));
    }
}
