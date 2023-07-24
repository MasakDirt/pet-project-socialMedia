package com.social.media.service;

import com.social.media.exception.InvalidTextException;
import com.social.media.model.entity.Role;
import com.social.media.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class RoleServiceTests {
    private final RoleService roleService;

    private Set<Role> roles;

    @Autowired
    public RoleServiceTests(RoleService roleService) {
        this.roleService = roleService;
    }

    @BeforeEach
    public void setRoles() {
        roles = roleService.getAll();
    }

    @Test
    public void test_Injected_Component() {
        assertThat(roleService).isNotNull();
        assertThat(roles).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertTrue(roleService.getAll().size() > 0,
                "After getting all roles they size must be bigger than 0.");
        assertEquals(roles, roleService.getAll(),
                "Before and now getting posts must be th same!");
    }

    @Test
    public void test_Valid_Create() {
        String name = "NEW";
        Role expected = new Role();
        expected.setName(name);

        Role actual = roleService.create(name);
        expected.setId(actual.getId());

        assertTrue(roles.size() < roleService.getAll().size(),
                "Roles size must be smaller than after creating");
        assertEquals(expected, actual,
                "After creating, roles objects must be equal.");
    }

    @Test
    public void test_Invalid_Create() {
        assertAll(
                () -> assertThrows(InvalidTextException.class, () -> roleService.create("   "),
                        "Here must be InvalidTextException because role name cannot be 'blank'."),
                () -> assertThrows(InvalidTextException.class, () -> roleService.create(null),
                        "Here must be InvalidTextException because role name cannot be 'null'.")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        Role expected = roleService.create("ROLE");
        Role actual = roleService.readById(expected.getId());

        assertEquals(expected, actual,
                "After reading role by id, objects must be equal!");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> roleService.readById(0L),
                "Here must be EntityNotFoundException because we have not role with id 0.");
    }

    @Test
    public void test_Valid_Update() {
        long roleID = 2L;
        String newName = "UPDATED";

        Role old = roleService.readById(roleID);
        long oldId = old.getId();
        String oldName = old.getName();
        Set<User> oldUsers = old.getUsers();

        Role actual = roleService.update(roleID, newName);

        assertAll(
                () -> assertEquals(oldId, actual.getId(),
                        "Id`s after updating must not change!"),
                () -> assertEquals(oldUsers, actual.getUsers(),
                        "Set of users after updating must not change!"),
                () -> assertEquals(newName, actual.getName(),
                        "Names must be the same!"),

                () -> assertNotEquals(oldName, actual.getName(),
                        "Names after updating must change!")
        );
    }

    @Test
    public void test_Invalid_Update() {
        long roleId = 1L;
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> roleService.update(0L, "READER"),
                        "Here must be EntityNotFoundException because we have not role with id 0."),

                () -> assertThrows(InvalidTextException.class, () -> roleService.update(roleId, "  "),
                        "Here must be InvalidTextException because name cannot be 'blank'."),

                () -> assertThrows(InvalidTextException.class, () -> roleService.update(roleId, null),
                        "Here must be InvalidTextException because name cannot be 'null'.")
        );
    }

    @Test
    public void test_Valid_Delete() {
        roleService.delete(2L);

        assertTrue(roles.size() > roleService.getAll().size(),
                "After deleting roles size must be bigger!");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> roleService.delete(0L),
                "Here must be EntityNotFoundException because we have not role with id 0.");
    }

    @Test
    public void test_Valid_ReadByName() {
        String name = "FOR_READ";
        Role expected = roleService.create(name);
        Role actual = roleService.readByName(name);

        assertEquals(expected, actual,
                "After reading by name objects of roles must be equals!");
    }

    @Test
    public void test_Invalid_ReadByName() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> roleService.readByName("NOT_FOUND"),
                        "Here must be EntityNotFoundException because role with name 'NOT_FOUND' does not exist."),

                () -> assertThrows(InvalidTextException.class, () -> roleService.readByName("   "),
                        "Here must be InvalidTextException because role name cannot be 'blank'."),

                () -> assertThrows(InvalidTextException.class, () -> roleService.readByName(null),
                        "Here must be InvalidTextException because role name cannot be 'null'.")
        );
    }
}
