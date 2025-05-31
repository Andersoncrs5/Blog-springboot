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
public class CommentMetricsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.commentLikeRepository.deleteAll();
        this.unit.postLikeRepository.deleteAll();
        this.unit.favoriteCommentRepository.deleteAll();
        this.unit.commentRepository.deleteAll();
        this.unit.favoritePostRepository.deleteAll();
        this.unit.postRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldGetMetricOfComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(get("/v1/comment/getMetric/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Metric got with successfully"));
    }

    @Test
    public void shouldCheckLikeInComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(post("/v1/commentLike/" + LikeOrUnLike.LIKE + "/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/comment/getMetric/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Metric got with successfully"))
                .andExpect(jsonPath("$.result.likes").isNumber())
                .andExpect(jsonPath("$.result.likes").value(1));
    }

    @Test
    public void shouldCheckDislikeInComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(post("/v1/commentLike/" + LikeOrUnLike.UNLIKE + "/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/comment/getMetric/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Metric got with successfully"))
                .andExpect(jsonPath("$.result.dislikes").isNumber())
                .andExpect(jsonPath("$.result.dislikes").value(1));
    }

    @Test
    public void shouldCheckCountFavorites() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        FavoriteCommentTestUtil.saveCOmmentHowFavorite(mockMvc, commentId, token);

        mockMvc.perform(get("/v1/comment/getMetric/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Metric got with successfully"))
                .andExpect(jsonPath("$.result.favorites").isNumber())
                .andExpect(jsonPath("$.result.favorites").value(1));
    }

}


