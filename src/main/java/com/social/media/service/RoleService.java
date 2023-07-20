package com.social.media.service;

import com.social.media.model.entity.Role;
import com.social.media.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role create(@NotNull Role role) {
        return roleRepository.save(role);
    }

    public Role readById(long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Role with id" + id + "not found!"));
    }

    public Role update(@NotNull Role updatedRole) {
        readById(updatedRole.getId());
        return roleRepository.save(updatedRole);
    }

    public void delete(long id){
        roleRepository.delete(readById(id));
    }

    public Role readByName(
            @NotNull @NotBlank(message = "Role name cannot be blank.")
            String name
    ) {
        return roleRepository.findByName(name).orElseThrow(() ->
                new EntityNotFoundException("Role with name" + name + "not found!"));
    }
}
