package com.social.media.controller;

import com.social.media.model.dto.auth.LoginRequestWithEmail;
import com.social.media.model.dto.auth.LoginRequestWithUsername;
import com.social.media.model.dto.photo.PhotoResponse;
import com.social.media.model.entity.Photo;
import com.social.media.model.mapper.PhotoMapper;
import com.social.media.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.social.media.controller.StaticHelperForMVC.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PhotoControllerTests {
    private static final String BASE_URL = "/api/users/{owner-id}/posts/{post-id}/photos";

    private final MockMvc mvc;
    private final PhotoService photoService;
    private final PhotoMapper mapper;

    private String tokenAdmin;
    private String tokenUser;

    @Autowired
    public PhotoControllerTests(MockMvc mvc, PhotoService photoService, PhotoMapper mapper) {
        this.mvc = mvc;
        this.photoService = photoService;
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
                        asJsonString(new LoginRequestWithEmail("garry@mail.co", "2222"))
                )
        ).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(mvc).isNotNull();
        assertThat(photoService).isNotNull();
    }

    @Test
    public void test_Valid_GetAllPhotosUnderPost_UserAuth() throws Exception {
        testValidGetAllPhotosUnderPost(2L, 5L, tokenAdmin);
    }

    @Test
    public void test_Valid_GetAllPhotosUnderPost_AdminAuth() throws Exception {
        testValidGetAllPhotosUnderPost(3L, 2L, tokenAdmin);
    }

    private void testValidGetAllPhotosUnderPost(long ownerId, long postId, String token) throws Exception {
        List<PhotoResponse> expected = photoService.getAllByPost(postId)
                .stream()
                .map(mapper::createPhotoResponseFromPhoto)
                .toList();

        mvc.perform(get(BASE_URL, ownerId, postId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty().contains(asJsonString(expected).substring(0, 20))
                );
    }

    @Test
    public void test_Invalid_GetAllPhotosUnderPost_AdminAuth_UserIsNotOwnerOfPost() throws Exception {
        mvc.perform(get(BASE_URL, 1L, 3L)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_GetPhotoUnderPost_UserAuth() throws Exception {
        long ownerId = 1L;
        long postId = 1L;
        long photoId = 1L;

        PhotoResponse expected = mapper.createPhotoResponseFromPhoto(photoService.readById(photoId));

        mvc.perform(get(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString().substring(0, 20), asJsonString(expected).substring(0, 20),
                                "Photos,must be equal because they read by same id," +
                                        " substring used because file path has Cyrillic letters, that not equal in 'result'.")
                );
    }

    @Test
    public void test_Invalid_GetPhotoUnderPost_AdminAuth_UserIsNotOwnerOfPost() throws Exception {
        long ownerId = 1L;
        long postId = 3L;
        long photoId = 4L;

        mvc.perform(get(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_GetPhotoUnderPost_UserAuth_PostIsNotContainPhoto() throws Exception {
        long ownerId = 3L;
        long postId = 2L;
        long photoId = 6L;

        mvc.perform(get(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Valid_DeletePhotoUnderPost_AdminAuth() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long photoId = 5L;

        List<Photo> beforeDeleting = photoService.getAllByPost(postId);
        Photo photo = photoService.readById(photoId);

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                String.format("Photo in - %s post - '%s' successfully deleted",
                                        photo.getPost().getOwner().getName(), photo.getPost().getDescription()),
                                "After deleting user must get this message!")
                );

        assertTrue(beforeDeleting.size() > photoService.getAllByPost(postId).size(),
                "Size of all post photos before deleting must be bigger!");
    }

    @Test
    public void test_Invalid_DeletePhotoUnderPost_AdminAuth_LastPhoto() throws Exception {
        long ownerId = 1L;
        long postId = 4L;
        long photoId = 6L;

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                      assertThat(result.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                              .contains("In post must be at least one photo!")
                );
    }

    @Test
    public void test_Invalid_DeletePhotoUnderPost_AdminAuth_AuthUserAndOwnerIsNotSames() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long photoId = 4L;

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeletePhotoUnderPost_UserAuth_UserIsNotOwnerOfPost() throws Exception {
        long ownerId = 2L;
        long postId = 2L;
        long photoId = 4L;

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }

    @Test
    public void test_Invalid_DeletePhotoUnderPost_UserAuth_PostIsNotContainPhoto() throws Exception {
        long ownerId = 2L;
        long postId = 3L;
        long photoId = 2L;

        mvc.perform(delete(BASE_URL + "/{id}", ownerId, postId, photoId)
                        .header("Authorization", "Bearer " + tokenUser)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isNotEmpty().isNotNull().contains("Access Denied")
                );
    }
}
