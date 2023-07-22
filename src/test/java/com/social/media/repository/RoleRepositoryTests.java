package com.social.media.repository;

import com.social.media.model.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class RoleRepositoryTests {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleRepositoryTests(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Test
    public void test_Injected_Component() {
        assertThat(roleRepository).isNotNull();
    }

    @Test
    public void test_FindByName() {
        String name = "NAME";
        Role expected = new Role();
        expected.setName(name);

        expected = roleRepository.save(expected);

        Role actual = roleRepository.findByName(name).orElse(new Role());
        Assertions.assertEquals(expected, actual,
                "Roles after reading by name must be equal!");
    }
}
