package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.role.RoleResponse;
import com.social.media.model.entity.Role;
import com.social.media.model.mapper.RoleMapper;
import com.social.media.service.RoleService;
import com.social.media.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RoleControllerTests {
    public static final String BASE_URL = "/api/roles";

    private final MockMvc mvc;
    private final RoleService roleService;
    private final RoleMapper mapper;
    private final UserService userService;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public RoleControllerTests(MockMvc mvc, RoleService roleService,
                               RoleMapper mapper, UserService userService) {
        this.mvc = mvc;
        this.roleService = roleService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @BeforeEach
    void setUp() throws Exception {
        tokenAdmin = mvc.perform(post("/api/auth/login/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithUsername("skallet24", "1111"))
                )
        ).andReturn().getResponse().getContentAsString();

        tokenUser = mvc.perform(post("/api/auth/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        asJsonString(new LoginRequestWithEmail("olivia@mail.co", "3333"))
                )
        ).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_InjectedComponents() {
        assertThat(mvc).isNotNull();
        assertThat(roleService).isNotNull();
        assertThat(mapper).isNotNull();
    }

    @Test
    public void test_Valid_GetAllRoles_AdminAuthorization() throws Exception {
        List<RoleResponse> expected = roleService.getAll()
                .stream()
                .map(mapper::createRoleResponseFromEntity)
                .toList();

        mvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString())
                );
    }

    @Test
    public void test_Valid_GetAllRoles_UserAuthorization_AccessDenied() throws Exception {
        mvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetRoleById_AdminAuthorization() throws Exception {
        long roleId = 1L;
        RoleResponse expected = mapper.createRoleResponseFromEntity(roleService.readById(roleId));

        mvc.perform(get(BASE_URL + "/{id}", roleId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Roles that read bt same id must be equal!")
                );
    }

    @Test
    public void test_Invalid_GetRoleById_UserAuthorization_AccessDenied() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 1L)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetRoleByName_AdminAuthorization() throws Exception {
        String roleName = "USER";
        RoleResponse expected = mapper.createRoleResponseFromEntity(roleService.readByName(roleName));

        mvc.perform(get(BASE_URL + "/name/{name}", roleName)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Roles that read by same name must be equal!")
                );
    }

    @Test
    public void test_Invalid_GetRoleByName_UserAuthorization_AccessDenied() throws Exception {
        mvc.perform(get(BASE_URL + "/name/{name}", "USER")
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetMyRole_AdminAuthorization() throws Exception {
        testValidGetMyRole(2L, tokenAdmin);
    }

    @Test
    public void test_Valid_GetMyRole_UserAuthorization() throws Exception {
        testValidGetMyRole(3L, tokenUser);
    }

    private void testValidGetMyRole(long userId, String token) throws Exception {
        RoleResponse expected = mapper.createRoleResponseFromEntity(userService.readById(userId).getRole());

        mvc.perform(get(BASE_URL + "/user/{user-id}", userId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Roles that reads by same user id, must be equal!")
                );
    }

    @Test
    public void test_Invalid_GetMyRole_UserAuthorization_AuthUserNotEqualToChecking() throws Exception {
        mvc.perform(get(BASE_URL + "/user/{user-id}", 2L)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_CreateNew_AdminAuthorization() throws Exception {
        String newRoleName = "NEWROLE";

        Role role = new Role();
        role.setId(3L);
        role.setName(newRoleName);

        RoleResponse expected = mapper.createRoleResponseFromEntity(role);

        mvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("name", newRoleName)
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(asJsonString(expected), result.getResponse().getContentAsString(),
                                "Created roles must be the sames")
                );
    }

    @Test
    public void test_Invalid_CreateNew_UserAuthorization_AccessDenied() throws Exception {
        mvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("name", "ROLE")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_UpdateRole_AdminAuth() throws Exception {
        long roleID = 2L;
        RoleResponse unexpected = mapper.createRoleResponseFromEntity(roleService.readById(roleID));
        String oldName = unexpected.getName();

        String newName = "ROLE";

        mvc.perform(put(BASE_URL + "/{id}", roleID)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("name", newName)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertNotEquals(asJsonString(unexpected), result.getResponse().getContentAsString(),
                                "After updating roles must be different.")
                );

        Role afterUpdating = roleService.readById(roleID);

        assertEquals(unexpected.getId(), afterUpdating.getId(),
                "After updating role id must be the same.");

        assertThat(oldName).isNotEqualTo(afterUpdating.getName());
        assertThat(newName).isEqualTo(afterUpdating.getName());
    }

    @Test
    public void test_Invalid_UpdateRole_UserAuth_AccessDenied() throws Exception {
        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("name", "NAME")
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_DeleteRole_AdminAuth() throws Exception {
        long roleId = 2L;
        Role role = roleService.readById(roleId);
        Set<Role> beforeDeleting = roleService.getAll();

        mvc.perform(delete(BASE_URL + "/{id}", roleId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                String.format("Role with name '%s' successfully deleted!", role.getName()),
                                "After deleting response must contain this message!")
                );

        assertTrue(beforeDeleting.size() > roleService.getAll().size(),
                "After deleting roles size must be smaller than before.");
    }

    @Test
    public void test_Invalid_DeleteRole_UserAuth_AccessDenied() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains("Access Denied")
                );
    }
}
