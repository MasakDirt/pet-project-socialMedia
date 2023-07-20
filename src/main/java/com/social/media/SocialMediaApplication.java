package com.social.media;

import com.social.media.model.entity.*;
import com.social.media.service.*;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class SocialMediaApplication implements CommandLineRunner {
    private final UserService userService;
    private final PostService postService;
    private final RoleService roleService;
    private final CommentService commentService;
    private final LikeService likeService;

    private static void writeInPropertiesFile() {
        String username = System.getenv("username");
        String password = System.getenv("password");
        String propertiesFilePath = "src/main/resources/application.properties";

        if (username == null || password == null) {
            log.warn("You need to write your username and password in Environment Variables!");
            return;
        }

        inputAndOutputInProperties(username, password, propertiesFilePath);
    }

    private static void inputAndOutputInProperties(String username, String password, String propertiesFilePath) {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(propertiesFilePath);
            properties.load(input);
            input.close();

            properties.setProperty("spring.datasource.username", username);
            properties.setProperty("spring.datasource.password", password);
            properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/my_social_media");

            FileOutputStream output = new FileOutputStream(propertiesFilePath);
            properties.store(output, null);

            output.flush();
            output.close();

            log.info("Username and password was successfully written in file application.properties.");
        } catch (IOException io) {
            log.error("Error: writing in property file {}", io.getMessage());
        }
    }

    public static void main(String[] args) {
        writeInPropertiesFile();
        SpringApplication.run(SocialMediaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("SocialMediaApplication has been started!!!");

        Role admin = createRole("ADMIN");
        Role user = createRole("USER");

        creatingUsers(admin, user);
    }

    private void creatingUsers(Role admin, Role userRole) throws Exception {
//      USERS
        long adminId = createUser("Vito", "Jones", "skallet24", "jone@mail.co", "1111", admin);
        long garryId = createUser("Garry", "Thomas", "garry.potter", "garry@mail.co", "2222", userRole);
        long oliviaId = createUser("Olivia", "Rodriguez", "oil", "olivia@mail.co", "3333", userRole);

//      POSTS
        long post1Id = createPost(adminId, "My first photo", "photos/nature-photography.webp");
        long post2Id = createPost(oliviaId, "My caaaat♡♡♡", "photos/catshark.webp");
        long post3Id = createPost(garryId, "Machine that I want", "photos/mcLaren.jpg");
        long post4Id = createPost(adminId, "Look which girl I have been drown", "photos/girl.webp");
        long post5Id = createPost(garryId, "I really like green color☺☺☺", "photos/green.jpg");

//      COMMENTS
        createComment(garryId, post1Id, "Nice photo");

        createComment(garryId, post2Id, "Ohh, it`s so cute...");
        createComment(adminId, post2Id, "I like cats!");

        createComment(garryId, post3Id, "I have no comments");
        createComment(oliviaId, post3Id, "Are you crazy!?");

        createComment(garryId, post4Id, "Woooow, it`s amazing!!");
        createComment(oliviaId, post4Id, "Are you crazy!?");

        createComment(oliviaId, post5Id, "Looking like a grass㋛");

//      LIKES
        createLike(oliviaId, post1Id);

        createLike(oliviaId, post2Id);
        createLike(garryId, post2Id);
        createLike(adminId, post2Id);

        createLike(adminId, post3Id);
        createLike(garryId, post3Id);
        createLike(oliviaId, post3Id);

        createLike(garryId, post4Id);
        createLike(oliviaId, post4Id);

        createLike(adminId, post5Id);
        createLike(garryId, post5Id);
        createLike(oliviaId, post5Id);
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);

        var created = roleService.create(role);
        log.info("Role with name {} was created!", name);
        return created;
    }

    private long createUser(String firstName, String lastName, String username, String email, String password, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        var created = userService.create(user);
        log.info("User {} successfully created", created.getName());
        return created.getId();
    }

    private long createPost(long ownerId, String description, String photoFile) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var created = postService.create(ownerId, description, photoFile);
        log.info("{} posted photo with description {}", created.getOwner().getName(), created.getDescription());
        return created.getId();
    }

    private void createComment(long ownerId, long postId, String comment) {
        Comment commentObj = new Comment();
        commentObj.setComment(comment);

        var created = commentService.create(ownerId, postId, commentObj);
        log.info("{} post has been successfully commented by {}", created.getPost().getOwner().getName(), created.getOwner().getName());
    }

    private void createLike(long ownerId, long postId) {
        var created = likeService.create(ownerId, postId);
        log.info("{} post has been successfully liked by {}", created.getPost().getOwner().getName(), created.getOwner().getName());
    }
}
