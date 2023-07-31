package com.social.media.model.mapper;

import com.social.media.model.dto.role.RoleResponse;
import com.social.media.model.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse createRoleResponseFromEntity(Role role);
}
