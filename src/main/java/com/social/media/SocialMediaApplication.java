package com.social.media;

import com.social.media.model.entity.*;
import com.social.media.mongo.MongoClientConnection;
import com.social.media.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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
    private final MessengerService messengerService;
    private final MessageService messageService;

    private static final String CONNECTION_MONGO = System.getenv("connection");

    private static void writeInPropertiesFile() {
        String username = System.getenv("username");
        String password = System.getenv("password");

        if (username == null || password == null) {
            log.warn("You need to write your username and password in Environment Variables!");
            return;
        }

        inputAndOutputInProperties(username, password);
    }

    private static void inputAndOutputInProperties(String username, String password) {
        Properties properties = new Properties();
        String propertiesFilePath = "src/main/resources/application.properties";

        try {
            FileInputStream input = new FileInputStream(propertiesFilePath);
            properties.load(input);
            input.close();

            properties.setProperty("spring.datasource.username", username);
            properties.setProperty("spring.datasource.password", password);
            properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/my_social_media");
            properties.setProperty("spring.data.mongodb.uri", CONNECTION_MONGO);

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
        MongoClientConnection.connectToMongoDb(CONNECTION_MONGO);

        SpringApplication.run(SocialMediaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("SocialMediaApplication has been started!!!");

        Role admin = createRole("ADMIN");
        Role user = createRole("USER");

        creatingUsers(admin, user);
    }

    private void creatingUsers(Role admin, Role userRole) {
//      USERS
        long adminId = createUser("Vito", "Jones", "skallet24", "jone@mail.co", "1111", admin);
        long garryId = createUser("Garry", "Thomas", "garry.potter", "garry@mail.co", "2222", userRole);
        long oliviaId = createUser("Olivia", "Rodriguez", "oil", "olivia@mail.co", "3333", userRole);

//      POSTS
        long post1Id = createPost(adminId, "My first photo", List.of("photos/nature-photography.webp"));
        long post2Id = createPost(oliviaId, "My caaaat`s♡♡♡", List.of("photos/catshark.webp", "photos/small_cat.jpg"));
        long post3Id = createPost(garryId, "Machines that I want", List.of("photos/mcLaren.jpg", "photos/bmwi4.jpg"));
        long post4Id = createPost(adminId, "Look which girl I have been drown", List.of("photos/girl.webp"));
        long post5Id = createPost(garryId, "I really like green color☺☺☺", List.of("photos/green.jpg"));

//      COMMENTS
        createComment(garryId, post1Id, "Nice photo");

        createComment(garryId, post2Id, "Ohh, it`s so cute...");
        createComment(adminId, post2Id, "I like cats!");

        createComment(garryId, post3Id, "I have no comments");

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

//      MESSENGERS
        long adminWithGarryMessengerId = createMessenger(adminId, garryId);
        long garryWithOliviaMessengerId = createMessenger(garryId, oliviaId);
        long oliviaWithAdminMessengerId = createMessenger(oliviaId, adminId);

        long garryWithAdminMessengerId = messengerService.readByOwnerAndRecipient(garryId, adminId).getId();
        long oliviaWithGarryMessengerId = messengerService.readByOwnerAndRecipient(oliviaId, garryId).getId();
        long adminWithOliviaMessengerId = messengerService.readByOwnerAndRecipient(adminId, oliviaId).getId();

//      MESSAGES
        createMessage(adminWithGarryMessengerId, "Hi, how are you?");
        createMessage(garryWithAdminMessengerId, "Hi, I`m fine and you?");
        createMessage(adminWithGarryMessengerId, "Nice, thanks.");

        createMessage(garryWithOliviaMessengerId, "Wow, I like that new Social Media, and you?");
        createMessage(oliviaWithGarryMessengerId, "I like it, too)))");

        createMessage(oliviaWithAdminMessengerId, "Today, I will go to the hospital, can you lift me?");
        createMessage(adminWithOliviaMessengerId, "Of course, tell me what time please.");
        createMessage(oliviaWithAdminMessengerId, "At 15.00");
    }

    private Role createRole(String name) {
        var created = roleService.create(name);
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

    private long createPost(long ownerId, String description, List<String> photoFile) {
        var created = postService.create(ownerId, description, photoFile);
        log.info("{} posted photo with description {}", created.getOwner().getName(), created.getDescription());
        return created.getId();
    }

    private void createComment(long ownerId, long postId, String comment) {
        var created = commentService.create(ownerId, postId, comment);
        log.info("{} post has been successfully commented by {}", created.getPost().getOwner().getName(), created.getOwner().getName());
    }

    private void createLike(long ownerId, long postId) {
        var created = likeService.create(ownerId, postId);
        log.info("{} post has been successfully liked by {}", created.getPost().getOwner().getName(), created.getOwner().getName());
    }

    private long createMessenger(long ownerId, long recipientId) {
        var created = messengerService.create(ownerId, recipientId);
        log.info("Messenger between {} and {} has been created.", created.getOwner().getName(), created.getRecipient().getName());
        return created.getId();
    }

    private void createMessage(long messengerId, String message) {
        var created = messageService.create(messengerId, message);
        log.info("Message for {} has been created", messengerService.readById(created.getMessengerId()));
    }
}

