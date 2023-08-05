package com.social.media.config;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.user.UserCreateRequest;
import com.social.media.model.dto.user.UserResponse;
import com.social.media.model.entity.User;
import com.social.media.model.mapper.UserMapper;
import com.social.media.service.RoleService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static com.social.media.controller.StaticHelperForMVC.createUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTests {
    private final MockMvc mvc;

    private final UserMapper mapper;
    private final RoleService roleService;

    @Autowired
    public SecurityConfigTests(MockMvc mvc, UserMapper mapper, RoleService roleService) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.roleService = roleService;
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(mvc).isNotNull();
        AssertionsForClassTypes.assertThat(mapper).isNotNull();
        AssertionsForClassTypes.assertThat(roleService).isNotNull();
    }

    @Test
    public void test_SecuredUrl_LoginEmail() throws Exception {
        mvc.perform(post("/api/auth/login/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithEmail("garry@mail.co", "2222"))
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    public void test_SecuredUrl_LoginUsername() throws Exception {
        mvc.perform(post("/api/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequestWithUsername("oil", "3333"))
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    public void test_SecuredUrl_Register() throws Exception {
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
                        result.getResponse().getContentAsString().substring(8),
                        "This test must be equal, substring for not same id`s, because we create user in this url, so he has not 0 id.")
                );
    }

    @Test
    public void test_SecuredUrl_WhichCannotBeAccessByUnauthorizedUser() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertEquals(result.getResponse().getErrorMessage(),
                        "Error: Unauthorized (please authorize before going to this URL," +
                                " go to '/api/auth/login/(username or email)' and authorize there, than return!)",
                        "Here must be a message, for better user understanding, why he can not go to this page.")
                );
    }

    @Test
    public void test_CorsConfig() throws Exception {
        mvc.perform(head("/**")).andExpect(status().isUnauthorized());
    }
}
