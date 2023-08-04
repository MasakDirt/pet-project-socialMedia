package com.social.media.service.authorization;

import com.social.media.model.entity.Like;
import com.social.media.service.LikeService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthLikeServiceTests {
    private final AuthLikeService authLikeService;
    private final LikeService likeService;

    @Autowired
    public AuthLikeServiceTests(AuthLikeService authLikeService, LikeService likeService) {
        this.authLikeService = authLikeService;
        this.likeService = likeService;
    }

    @Test
    public void test_InjectedComponents() {
        AssertionsForClassTypes.assertThat(authLikeService).isNotNull();
        AssertionsForClassTypes.assertThat(likeService).isNotNull();
    }

    @Test
    public void test_isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin_True() {
        assertTrue(authLikeService.isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin(1L, 4L, 9L, "oil"),
                "Here user owner of post and post contains like and auth user owner of like!");
    }

    @Test
    public void test_isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin_False() {
        assertFalse(authLikeService.isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin(1L, 3L, 9L, "oil"),
                "Here must be false because post is not user.");
        assertFalse(authLikeService.isUsersSameAndOwnerOfPostAndPostContainLikeWithoutAdmin(1L, 4L, 8L, "oil"),
                "Here must be false because like is not auth user.");
    }

    @Test
    public void test_GetLike() {
        long likeId = 5L;
        Like expected = likeService.readById(likeId);
        Like actual = authLikeService.getLike(likeId);

        assertEquals(expected, actual,
                "Likes read by the same id, so they must be the sames!");
    }
}
