package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Role;
import com.social.media.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role create(String name) {
        checkValidName(name);

        var role = new Role();
        role.setName(name);

        return roleRepository.save(role);
    }

    public Role readById(long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Role with id " + id + " not found!"));
    }

    public Role update(long roleId, String updatedName) {
        checkValidName(updatedName);

        var oldRole = readById(roleId);
        oldRole.setName(updatedName);

        return roleRepository.save(oldRole);
    }

    public void delete(long id) {
        roleRepository.delete(readById(id));
    }

    public Role readByName(String name) {
        checkValidName(name);

        return roleRepository.findByName(name).orElseThrow(() ->
                new EntityNotFoundException("Role with name " + name + " not found!"));
    }

    public Set<Role> getAll() {
        return new HashSet<>(roleRepository.findAll());
    }

    private void checkValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidTextException("Name of role must contain a word and cannot be blank");
        }
    }
}
