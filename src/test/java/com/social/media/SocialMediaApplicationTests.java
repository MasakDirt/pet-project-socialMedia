package com.social.media;

import com.social.media.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class SocialMediaApplicationTests {

	private final UserService userService;
	private final RoleService roleService;
	private final PostService postService;
	private final PhotoService photoService;
	private final MessengerService messengerService;
	private final MessageService messageService;
	private final LikeService likeService;
	private final CommentService commentService;

	@Autowired
	public SocialMediaApplicationTests(UserService userService, RoleService roleService, PostService postService,
									   PhotoService photoService, MessengerService messengerService,
									   MessageService messageService, LikeService likeService, CommentService commentService) {
		this.userService = userService;
		this.roleService = roleService;
		this.postService = postService;
		this.photoService = photoService;
		this.messengerService = messengerService;
		this.messageService = messageService;
		this.likeService = likeService;
		this.commentService = commentService;
	}

	@Test
	void contextLoads() {
		assertThat(userService).isNotNull();
		assertThat(roleService).isNotNull();
		assertThat(postService).isNotNull();
		assertThat(photoService).isNotNull();
		assertThat(messengerService).isNotNull();
		assertThat(messageService).isNotNull();
		assertThat(likeService).isNotNull();
		assertThat(commentService).isNotNull();
	}
}
