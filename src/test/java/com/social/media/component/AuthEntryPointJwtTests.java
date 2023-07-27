package com.social.media.component;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthEntryPointJwtTests {
    private final MockMvc mvc;

    @Autowired
    public AuthEntryPointJwtTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void test_Injected_MockMvc() {
        AssertionsForClassTypes.assertThat(mvc).isNotNull();
    }

    @Test
    public void test_UnauthorizedError_Commence() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/unauthorized")
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(
                        result -> Assertions.assertEquals(
                                "Unauthorized (Full authentication is required to access this resource)",
                                result.getResponse().getErrorMessage(),
                                "Result must return a valid message about 'unauthorized' user."
                        )
                );
    }
}
