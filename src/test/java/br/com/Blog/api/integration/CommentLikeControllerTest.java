package br.com.Blog.api.integration;

import br.com.Blog.api.entities.enums.LikeOrUnLike;
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
public class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        unit.favoriteCommentRepository.deleteAll();
        unit.commentLikeRepository.deleteAll();
        unit.commentRepository.deleteAll();
        unit.postRepository.deleteAll();
        unit.categoryRepository.deleteAll();
        unit.userRepository.deleteAll();
    }

    @Test
    public void shouldGetAllByUser() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId1 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId2 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId3 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId4 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId, token, objectMapper);
        CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId1, token, objectMapper);
        CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId2, token, objectMapper);
        CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId3, token, objectMapper);
        CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId4, token, objectMapper);

        mockMvc.perform(get("/v1/commentLike/getAllByUser")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void shouldExistsAction() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);
        long commentId1 = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId, token, objectMapper);

        mockMvc.perform(get("/v1/commentLike/exists/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.result").isBoolean())
                .andExpect(jsonPath("$.result").value(true));

        mockMvc.perform(get("/v1/commentLike/exists/" + commentId1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.result").isBoolean())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    public void shouldDeleteLike() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        String actionId = CommentLikeTestUtil.saveLikeOrDislikeInCommentAndReturnId(mockMvc, LikeOrUnLike.LIKE, commentId, token, objectMapper);

        mockMvc.perform(delete("/v1/commentLike/" + actionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    public void shouldReactWithLikeInComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(post("/v1/commentLike/" + LikeOrUnLike.LIKE + "/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldReactWithDislikeInComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(post("/v1/commentLike/" + LikeOrUnLike.LIKE + "/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

    }

}
