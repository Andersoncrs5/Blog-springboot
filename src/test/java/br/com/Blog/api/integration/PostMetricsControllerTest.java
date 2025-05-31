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
public class PostMetricsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.postLikeRepository.deleteAll();
        this.unit.favoriteCommentRepository.deleteAll();
        this.unit.commentRepository.deleteAll();
        this.unit.favoritePostRepository.deleteAll();
        this.unit.postRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldGetMetric() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        mockMvc.perform(get("/v1/posts/getMetric/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post metric found with successfully"));
    }

    @Test
    public void shouldChecklikes() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        mockMvc.perform(post("/v1/postLike/" + LikeOrUnLike.LIKE + "/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reaction added successfully"));

        mockMvc.perform(get("/v1/posts/getMetric/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post metric found with successfully"))
                .andExpect(jsonPath("$.result.likes").value(1));
    }

    @Test
    public void shouldCheckDislikes() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        mockMvc.perform(post("/v1/postLike/" + LikeOrUnLike.UNLIKE + "/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reaction added successfully"));

        mockMvc.perform(get("/v1/posts/getMetric/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post metric found with successfully"))
                .andExpect(jsonPath("$.result.dislikes").value(1));
    }

    @Test
    public void shouldCheckCommentCount() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentTestUtil.createMultiComment(mockMvc, objectMapper, token, categoryId, postId, 19);

        mockMvc.perform(get("/v1/posts/getMetric/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post metric found with successfully"))
                .andExpect(jsonPath("$.result.comments").value(20));
    }

    @Test
    public void shouldCheckFavoritesCount() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId  = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId , token);

        mockMvc.perform(get("/v1/posts/getMetric/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post metric found with successfully"))
                .andExpect(jsonPath("$.result.favorites").value(1));
    }

}