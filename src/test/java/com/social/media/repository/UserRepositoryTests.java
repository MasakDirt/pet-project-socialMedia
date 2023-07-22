package com.social.media.repository;

import com.social.media.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserRepositoryTests {
    private final UserRepository userRepository;
    private User expected;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void test_Injected_Component() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void test_FindByUsername() {
        String username = "to.find";
        expected = new User();
        expected.setUsername(username);
        expected.setEmail("for@mail.co");
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setPassword("newPass");

        expected = userRepository.save(expected);

        User actual = userRepository.findByUsername(username).orElse(new User());
        assertEquals(expected, actual,
                "Users after reading by username must be equal!");
    }

    @Test
    public void test_FindByEmail() {
        String email = "for@mail.co";
        expected = new User();
        expected.setUsername("username");
        expected.setEmail(email);
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setPassword("newPass");

        expected = userRepository.save(expected);

        User actual = userRepository.findByEmail(email).orElse(new User());
        assertEquals(expected, actual,
                "Users after reading by email must be equal!");
    }
}
