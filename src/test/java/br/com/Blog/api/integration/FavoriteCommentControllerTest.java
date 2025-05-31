package br.com.Blog.api.integration;

import br.com.Blog.api.integration.utils.*;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FavoriteCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        unit.favoriteCommentRepository.deleteAll();
        unit.favoritePostRepository.deleteAll();
        unit.postRepository.deleteAll();
        unit.categoryRepository.deleteAll();
        unit.userRepository.deleteAll();
    }

    @Test
    public void shouldExistsFavoriteComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId1 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        String favoriteId = FavoriteCommentTestUtil.saveCommentHowFavoriteAndReturnId(mockMvc, commentId, token, objectMapper);

        mockMvc.perform(get("/v1/favoriteComment/exists/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isBoolean())
                .andExpect(jsonPath("$.result").value(true));

        mockMvc.perform(get("/v1/favoriteComment/exists/" + commentId1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isBoolean())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    public void shouldDeleteFavoriteComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        String favoriteId = FavoriteCommentTestUtil.saveCommentHowFavoriteAndReturnId(mockMvc, commentId, token, objectMapper);

        mockMvc.perform(delete("/v1/favoriteComment/" + favoriteId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment removed with favorite!"));
    }

    @Test
    public void shouldCreateFavoriteCOmment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(post("/v1/favoriteComment/add/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Comment has been favorited successfully!!"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.user").exists())
                .andExpect(jsonPath("$.result.comment").exists());
    }

    @Test
    public void shouldGetAllFavoriteOfUser() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId1 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId2 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId3 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId4 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId5 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId6 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId7 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId1, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId2, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId3, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId4, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId5, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId6, token);
        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId7, token);

        mockMvc.perform(get("/v1/favoriteComment/GetAllFavoriteOfUser")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalElements").value(8))
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

}
