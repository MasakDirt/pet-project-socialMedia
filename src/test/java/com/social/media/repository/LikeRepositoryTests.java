package com.social.media.repository;

import com.social.media.model.entity.Like;
import com.social.media.model.entity.Post;
import com.social.media.model.entity.User;
import com.social.media.service.UserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class LikeRepositoryTests {
    private final LikeRepository likeRepository;
    private final UserService userService;

    @Autowired
    public LikeRepositoryTests(LikeRepository likeRepository, UserService userService){
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Test
    public void test_InjectedComponent() {
        AssertionsForClassTypes.assertThat(likeRepository).isNotNull();
    }

    @Test
    public void test_FindByOwnerAndPost() {
        User owner = userService.readById(2L);
        Post post = owner.getMyPosts().stream().findFirst().orElse(new Post());

        Like expected = new Like();
        expected.setPost(post);
        expected.setOwner(owner);

        Like actual = likeRepository.findByOwnerAndPost(owner, post);
        expected.setId(actual.getId());
        Assertions.assertEquals(expected, actual,
                "Likes after reading by owner and post must be equal!");
    }
}
