package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.user.UserCreateRequest;
import com.social.media.model.dto.user.UserResponse;
import com.social.media.model.entity.User;
import com.social.media.model.mapper.UserMapper;
import com.social.media.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static com.social.media.controller.StaticHelperForMVC.createUser;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthControllerTests {
    private final MockMvc mvc;

    private final UserMapper mapper;
    private final RoleService roleService;

    @Autowired
    public AuthControllerTests(MockMvc mvc, UserMapper mapper, RoleService roleService) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.roleService = roleService;
    }

    @Test
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(roleService).isNotNull();
    }

    @Test
    public void test_Valid_LoginEmail() throws Exception {
        mvc.perform(post("/api/auth/login/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithEmail("garry@mail.co", "2222"))
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    public void test_InvalidUsername_LoginEmail() throws Exception {
        mvc.perform(post("/api/auth/login/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithEmail("invalid@mail.co", "3333"))
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("User with email invalid@mail.co not found!")
                );
    }

    @Test
    public void test_InvalidPassword_LoginEmail() throws Exception {
        mvc.perform(post("/api/auth/login/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithEmail("jone@mail.co", "invalid"))
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Wrong password")
                );
    }

    @Test
    public void test_Valid_LoginUsername() throws Exception {
        mvc.perform(post("/api/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithUsername("oil", "3333"))
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    public void test_InvalidUsername_LoginUsername() throws Exception {
        mvc.perform(post("/api/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithUsername("invalid", "3333"))
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("User with username invalid not found!")
                );
    }

    @Test
    public void test_InvalidPassword_LoginUsername() throws Exception {
        mvc.perform(post("/api/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithUsername("garry.potter", "invalid"))
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Wrong password")
                );
    }

    @Test
    public void test_Register() throws Exception {
        UserCreateRequest userCreateRequest = createUser("new.user", "Firstname", "Lastname",
                "new@mail.co", "1234567890");

        User user = mapper.createUserFromUserCreateRequest(userCreateRequest);
        user.setRole(roleService.readByName("USER"));

        UserResponse expected = mapper.createUserResponseFromUser(user);

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(userCreateRequest)
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals(asJsonString(expected).substring(8),
                        result.getResponse().getContentAsString().substring(9),
                        "This test must be equal, substring for not same id`s, because we create user in this url, so he has not 0 id.")
                );
    }
}
