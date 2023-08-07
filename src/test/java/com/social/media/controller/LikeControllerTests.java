package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.mapper.LikeMapper;
import com.social.media.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class LikeControllerTests {
    private static final String BASE_URL = "/api";

    private final MockMvc mvc;
    private final LikeService likeService;
    private final LikeMapper mapper;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public LikeControllerTests(MockMvc mvc, LikeService likeService, LikeMapper mapper) {
        this.mvc = mvc;
        this.likeService = likeService;
        this.mapper = mapper;
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
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(likeService).isNotNull();
        assertThat(mapper).isNotNull();
    }

}
