# My Social Media - 'Keriton'

This is my simple social media example project to introduce you to my acquired skills and abilities. This project
is based on Rest Api and Spring framework using JWT token for authentication, and it is about a social media where
user can create his account where he can have multiple messengers between other users and created posts in which other users
can set likes and write or read comments.

## Table of Contents

- [Installation](#installation)
- [Users data](#users-data)
- [Main class](#main-class-socialmediaapplication)
- [Connection to mongo DB](#mongodb-connection-utility)
- [MinIO client](#minio-client-implementation)
- [Entity classes](#entity-classes)
- [Repository interfaces](#repository-interfaces)
- [Service classes](#services)
- [Authorization service classes](#services)
- [Component Descriptions](#component-descriptions)
- [Configuration classes](#configuration-classes)
- [Controller classes](#controllers)
- [Conclusion](#conclusion)

----------------------------------------------------------------------------------------------------------------------

## Installation

In my project, as you see, I have already committed pom.xml and app.properties files,
so you only need to clone my repository to your PC and create in your mySql db table with name:
"`my_social_media`" and create Configuration which will starts with
and added to field "Environment variables" something like that:

`connection=your_mongoDB_collection_url;password=your_pass;
username=your_useranme` where you need to write instead of your_password, password that you indicated in db settings
(likewise with username and connection).
After that, you need to run the project to initialize the password and username in the application.properties file, and then run it a second time
after which the project will work correctly!

$ git clone https://github.com/MasakDirt/pet-project-socialMedia.git

## "Users data":
| № |   Username   |     E-mail     | Password | Role  |
|:-:|:------------:|:--------------:|:--------:|:-----:|
| 1 |  skallet24   |  jone@mail.co  |   1111   | ADMIN |
| 2 | garry.potter | garry@mail.co  |   2222   | USER  |
| 3 |     oil      | olivia@mail.co |   3333   | USER  |

## Main Class (`SocialMediaApplication`)

The `SocialMediaApplication` class is the core of the Social Media Application, developed using Java Spring Boot.
It coordinates essential services such as user management, post creation, commenting, liking, and private messaging.
The class establishes connections to both MySQL and MongoDB databases for seamless data storage. 
During startup, it securely records user credentials in the application properties file, while also initializing sample user data, posts, comments, likes,
and messages for illustration. The application fosters user engagement through diverse interaction options, including posts, comments, likes, and direct messaging.


## MongoDB Connection Utility

This utility class, `MongoClientConnection`, provides a streamlined way to establish a connection
to a MongoDB database and perform initial setup tasks. It is particularly useful for setting up and clearing
a collection within the database. The class uses the `@Slf4j` annotation for logging.

## Minio Client Implementation
The `MinioClientImpl` class is a component annotated with `@Component` that facilitates interactions with a MinIO server. 
It includes methods for creating user-specific buckets, uploading and downloading photos, 
checking bucket and directory existence, retrieving a list of buckets, and configuring the MinIO client. 
This class relies on the MinIO Java SDK for communication and employs `SLF4J` for logging.

## Entity Classes

### Comment Entity (`Comment`)

The `Comment` entity class represents user comments on posts. It includes attributes such as `id` for unique identification,
`comment` to store the content of the comment, `timestamp` to track the comment's creation time,
`owner` referencing the user who posted the comment, and `post` referencing the associated post.
The class defines methods to check equality, generate hash codes, and provide a string representation of the comment.

### Like Entity (`Like`)

The `Like` entity class embodies user likes on posts.
It holds an `id` for uniqueness, an `owner` referencing the user who liked the post,
and a `post` referencing the liked post. The class provides methods to determine equality,
calculate hash codes, and create a string representation of the like.

### Message Entity (`Message`)

The `Message` entity class represents messages exchanged in private conversations.
It features a unique `id`, the `message` content, a `timestamp` indicating when the message was sent,
a `messengerId` identifying the related conversation, and an `ownerId` referring to the message sender.
The class implements methods for equality comparisons, hash code calculation, and generating a human-readable string representation.

### Messenger Entity (`Messenger`)

The `Messenger` entity class represents private conversations between users.
It includes an `id` for uniqueness, an `owner` indicating the conversation initiator,
and a `recipient` referencing the conversation recipient. The class defines methods for equality checks,
hash code generation, and creating a string representation.

### Photo Entity (`Photo`)

The `Photo` entity class represents images associated with posts. 
It includes attributes such as `id`, a `file` referencing the photo file, 
and a `post` connecting the photo to its corresponding post. The class defines methods for equality comparisons, 
hash code calculation, and creating a string representation.

### Post Entity (`Post`)

The `Post` entity class represents user-generated posts. It includes a `timestamp` indicating when the post was created,
a `description` for post content, an `owner` referencing the user who created the post,
collections of associated `comments`, `likes`, and `photos`. The class implements methods for checking equality,
calculating hash codes, and creating a string representation.

### Role Entity (`Role`)

The `Role` entity class represents user roles within the application. 
It features an `id`, a `name` describing the role, and a collection of `users` associated with the role.
The class implements `GrantedAuthority` for role-based authorization, and provides methods for equality comparisons,
hash code calculation, and creating a string representation.

### User Entity (`User`)

The `User` entity class represents users of the application. It includes attributes like `id`,
`username`, `firstName`, `lastName`, `email`, and `password` for authentication. 
It also references a `role`, and various collections related to user activity such as `myMessengers`, `myPosts`, 
`myComments`, `myLikes`, and `messagesToMe`. The class implements `UserDetails` for user authentication and authorization, 
and provides methods for equality checks, hash code calculation, and creating a string representation.

## Repository Interfaces

### UserRepository

The `UserRepository` interface extends `JpaRepository` and facilitates database operations for user entities.
It provides methods for finding users by `username`, `email`, or a combination of both.
Additional methods are available to retrieve users by `lastName`, `firstName`, and `roleName`.

### RoleRepository

The `RoleRepository` interface extends `JpaRepository` and handles database operations related 
to role entities. It offers a method to find roles by their `name`.

### PostRepository

The `PostRepository` interface, an extension of `JpaRepository`, manages database interactions concerning post entities.
It includes methods for retrieving posts by `ownerId` and for finding posts by both `ownerId` and `id`.

### PhotoRepository

The `PhotoRepository` interface, extending `JpaRepository`, manages database interactions for photo entities.
It offers methods to retrieve photos by their associated `postId`.

### MessengerRepository

The `MessengerRepository` interface, extending `JpaRepository`, handles database operations for messenger entities.
It includes methods to find messengers by `ownerId` and `recipientId`, as well as to retrieve messengers by `ownerId`.

### MessageRepository

The `MessageRepository` interface, annotated with `@EnableMongoRepositories`, extends `MongoRepository` and 
manages interactions with message entities stored in MongoDB. It provides methods to retrieve messages 
by their associated `messengerId`.

### LikeRepository

The `LikeRepository` interface, extending `JpaRepository`, manages database operations for like entities.
It includes methods to find likes by the `owner` and the `post`, retrieve likes by `postId`, and find likes by `ownerId`.

### CommentRepository

The `CommentRepository` interface, extending `JpaRepository`, handles database interactions for comment entities.
It offers methods to retrieve comments by their associated `postId` and `ownerId`.

## Services

### UserService

The `UserService` class provides methods to manage user-related operations. 
It interacts with the `UserRepository` and handles functions such as creating users, reading user details by various criteria,
updating user information, and deleting users.

### RoleService

The `RoleService` class handles role-related operations. 
It communicates with the `RoleRepository` and supports functions like creating roles, 
reading roles by various criteria, updating role information, and deleting roles.

### PostService

The `PostService` class manages post-related operations. It interacts with the `PostRepository`, 
`UserService`, `PhotoService`, and `MinioClientImpl` (for photo storage) to support functionalities such as 
creating posts with associated photos, reading posts by different criteria, updating post information, and deleting posts.

### PhotoService

The `PhotoService` class handles photo-related operations. 
It works with the `PhotoRepository` and `MinioClientImpl` to enable functions like creating photos, 
reading photos by various criteria, updating photos, and deleting photos.

### MessengerService

The `MessengerService` class is responsible for messenger-related actions. 
It communicates with the `MessengerRepository` and `UserService` to support functions like creating messengers between users,
reading messengers by different criteria, and deleting messengers.

### MessageService

The `MessageService` class manages message-related actions. It interacts with the `MessageRepository` 
and `MessengerService` to enable functionalities such as creating messages within messengers, reading messages by
different criteria, updating messages, and deleting messages.

### LikeService

The `LikeService` class handles like-related operations. It communicates with the `LikeRepository`, 
`UserService`, and `PostService` to support functions like creating likes for posts, reading likes by different criteria,
checking if a like exists, and deleting likes.

### CommentService

The `CommentService` class manages comment-related operations. 
It interacts with the `CommentRepository`, `UserService`, and `PostService` to enable functionalities such as creating
comments for posts, reading comments by different criteria, updating comments, and deleting comments.

## AuthUserService

The `AuthUserService` class contains methods for user authorization checks. It interacts with the `UserService` and provides functions for validating user identities, roles, and ownership of accounts. These checks are used to determine whether a user has the necessary permissions to perform specific actions.

## Authorization services

### AuthPostService

The `AuthPostService` class focuses on post-related authorization. 
It builds upon the `AuthUserService` to validate ownership and permissions related to posts. 
This class ensures that users are authorized to modify or delete posts that they own.

### AuthPhotoService

The `AuthPhotoService` class extends the authorization to photos associated with posts. 
It works in conjunction with the `AuthPostService` to verify that users have the required permissions to access 
or manipulate photos belonging to posts they own.

### AuthMessengerService

The `AuthMessengerService` class handles authorization for messenger-related operations. 
It collaborates with the `AuthUserService` and `MessengerService` to confirm whether a user 
is authorized to interact with a specific messenger.

### AuthMessageService

The `AuthMessageService` class provides authorization checks for message-related operations. 
It utilizes the `AuthMessengerService` and `MessageService` to ensure that users have the necessary permissions 
to access or manage messages within messengers.

### AuthLikeService

The `AuthLikeService` class focuses on authorization for like-related actions. 
It works with the `LikeService`, `AuthUserService`, and `AuthPostService` to verify that users have 
the required permissions to interact with likes on posts they own.

### AuthCommentService

The `AuthCommentService` class manages authorization for comment-related actions. 
It collaborates with the `CommentService`, `AuthUserService`, and `AuthPostService` to confirm 
that users are authorized to access or manage comments associated with posts they own.


## Component Descriptions

### AuthEntryPointJwt

The `AuthEntryPointJwt` class is a Spring `@Component` responsible for handling the authentication entry point. 
It implements the `AuthenticationEntryPoint` interface, providing the `commence` method to 
respond when authentication fails. This class logs the "Content-Type" of the incoming request and 
sends an "Unauthorized" error response along with a message suggesting users to authorize themselves before 
accessing a particular URL. 
This can be achieved by visiting the '/api/auth/login/(username or email)' endpoint for authorization.

### AuthTokenFilter

The `AuthTokenFilter` class, marked as a Spring `@Component`, is a filter that ensures proper 
authentication of requests. Extending the `OncePerRequestFilter` class, 
it collaborates with `JwtUtils` and `UserService` to verify JWT tokens and establish authentication context. 
When processing requests, this filter checks for the presence of a JWT token in the "Authorization" 
header with the "Bearer" prefix. If a valid token is found, it sets the authentication context with user details 
extracted from the token. This component contributes to securing the authentication process in the application.

## Configuration classes

### AppConfig

- Manages application-wide configurations.
- Defines `passwordEncoder()` bean for secure password handling.
- Sets up CORS with `configurationSource()` for global usage.

### SecurityConfig

- Handles security settings.
- Requires `AuthTokenFilter`, `AuthEntryPointJwt`, and `CorsConfigurationSource` beans.
- Configures security using `SecurityFilterChain`:
    - Defines CORS and disables CSRF.
    - Handles authentication errors.
    - Sets authorization rules.
    - Uses `AuthTokenFilter` for token-based authentication.

## Controllers

### AuthController

The `AuthController` class handles authentication and user registration.

#### Endpoints:

- `POST /api/auth/login/username`: Log in with a username and password.
- `POST /api/auth/login/email`: Log in with an email and password.
- `POST /api/auth/register`: Register a new user.

### UserController

The `UserController` class manages user-related operations.

#### Endpoints:

- `GET /api/users`: Retrieve all users (admin only).
- `GET /api/users/first-name/{first-name}`: Retrieve users by first name (admin only).
- `GET /api/users/last-name/{last-name}`: Retrieve users by last name (admin only).
- `GET /api/users/role/{role}`: Retrieve users by role (admin only).
- `GET /api/users/{id}`: Retrieve a user by ID.
- `GET /api/users/username-email/{username-or-email}`: Retrieve a user by username or email.
- `POST /api/users`: Create a new admin user (admin only).
- `POST /api/users/custom-role`: Create a new user with a custom role (admin only).
- `PUT /api/users/{id}`: Update a user by ID.
- `PUT /api/users/username/{username}`: Update a user by username.
- `PUT /api/users/email/{email}`: Update a user by email.
- `PUT /api/users/name/{id}`: Update user names by ID.
- `PUT /api/users/name/username-email/{username-or-email}`: Update user names by username or email.
- `PUT /api/users/password/{id}`: Update user password by ID.
- `PUT /api/users/password/username-email/{username-or-email}`: Update user password by username or email.
- `DELETE /api/users/{id}`: Delete a user by ID.
- `DELETE /api/users/username-email/{username-or-email}`: Delete a user by username or email.

### RoleController

The `RoleController` class manages role-related operations.

#### Endpoints:

- `GET /api/roles`: Retrieve all roles (admin only).
- `GET /api/roles/{id}`: Retrieve a role by ID (admin only).
- `GET /api/roles/name/{name}`: Retrieve a role by name (admin only).
- `GET /api/roles/user/{user-id}`: Retrieve a user's role by user ID.
- `POST /api/roles`: Create a new role (admin only).
- `PUT /api/roles/{id}`: Update a role by ID (admin only).
- `DELETE /api/roles/{id}`: Delete a role by ID (admin only).

### PostController

The `PostController` class manages post-related operations.

#### Endpoints:

- `GET /api/posts`: Retrieve all posts (admin only).
- `GET /api/users/{owner-id}/posts`: Retrieve all posts for a user.
- `GET /api/users/{owner-id}/posts/{id}`: Retrieve a user's post by post ID.
- `POST /api/users/{owner-id}/posts`: Create a new post for a user.
- `PUT /api/users/{owner-id}/posts/{id}`: Update a post's description.
- `DELETE /api/users/{owner-id}/posts/{id}`: Delete a post by ID.

### PhotoController

The `PhotoController` class manages photo-related operations.

#### Endpoints:

- `GET /api/users/{owner-id}/posts/{post-id}/photos`: Retrieve all photos under a post.
- `GET /api/users/{owner-id}/posts/{post-id}/photos/{id}`: Retrieve a photo under a post.
- `DELETE /api/users/{owner-id}/posts/{post-id}/photos/{id}`: Delete a photo under a post.

### MessengerController

The `MessengerController` class manages messenger-related operations.

#### Endpoints:

- `GET /api/users/{owner-id}/messengers`: Retrieve all messengers for a user.
- `GET /api/users/{owner-id}/messengers/{id}`: Retrieve a messenger by ID for a user.
- `POST /api/users/{owner-id}/messengers`: Create a new messenger for a user.
- `DELETE /api/users/{owner-id}/messengers/{id}`: Delete a messenger by ID for a user.

### MessageController

The `MessageController` class manages message-related operations.

#### Endpoints:

- `GET /api/users/{owner-id}/messengers/{messenger-id}/messages`: Retrieve all messages for a messenger.
- `GET /api/users/{owner-id}/messengers/{messenger-id}/messages/{id}`: Retrieve a message by ID for a messenger.
- `POST /api/users/{owner-id}/messengers/{messenger-id}/messages`: Create a new message for a messenger.
- `PUT /api/users/{owner-id}/messengers/{messenger-id}/messages/{id}`: Update a message by ID for a messenger.
- `DELETE /api/users/{owner-id}/messengers/{messenger-id}/messages/{id}`: Delete a message by ID for a messenger.


### LikeController

The `LikeController` class manages operations related to user likes on posts.

#### Endpoints:

- `GET /api/users/{owner-id}/posts/{post-id}/likes`: Retrieves a list of likes under a specific post. Only the owner of the post is authorized to access this endpoint. The response includes a list of likes associated with the post.

- `GET /api/users/{owner-id}/likes`: Retrieves a list of likes owned by a specific user. The authenticated user must be the owner of the likes to access this endpoint. The response contains a list of likes associated with the user.

- `POST /api/users/{owner-id}/posts/{post-id}/likes`: Creates a new like on a post. The authenticated user needs to be the owner of the post to set a like. A success message is returned upon successful creation.

- `DELETE /api/users/{owner-id}/posts/{post-id}/likes/{id}`: Removes a like from a post. This endpoint requires the user to be the owner of the post and the like to delete it. A success message is returned if the deletion is successful.

### CommentController

The `CommentController` class handles operations related to user comments on posts.

#### Endpoints:

- `GET /api/users/{owner-id}/posts/{post-id}/comments`: Retrieves a list of comments under a specific post. Only the owner of the post is authorized to access this endpoint. The response includes a list of comments associated with the post.

- `GET /api/users/{owner-id}/comments`: Retrieves a list of comments owned by a specific user. The authenticated user must be the owner of the comments to access this endpoint. The response contains a list of comments associated with the user.

- `POST /api/users/{owner-id}/posts/{post-id}/comments`: Creates a new comment on a post. The authenticated user needs to be the owner of the post to post a comment. A success message is returned upon successful creation.

- `PUT /api/users/{owner-id}/posts/{post-id}/comments/{id}`: Updates an existing comment on a post. The user needs to be the owner of the comment and the post. A success message is returned upon successful update.

- `DELETE /api/users/{owner-id}/posts/{post-id}/comments/{id}`: Deletes a comment from a post. The user must be the owner of the comment and the post. A success message is returned upon successful deletion.

-------------------------------------------------------------------------------

## "Conclusion":

I really enjoyed developing my project, and I want to try myself on a real project as soon as possible.
Thank you for paying attention to my project.

I hope this was clear to you, and if not, you can contact me for further details:

Telegram: `@mskdrttt`

E-mail: `maksimkarulet8@gmail.com`.