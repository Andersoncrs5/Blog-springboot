package br.com.Blog.api.integration;

import br.com.Blog.api.DTOs.CommentDTO;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.integration.utils.CategoryTestUtils;
import br.com.Blog.api.integration.utils.CommentTestUtil;
import br.com.Blog.api.integration.utils.PostTestUtil;
import br.com.Blog.api.integration.utils.UserTestUtils;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldGetTheMetricComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentTestUtil.getCommentAndReturnMockResult(mockMvc, token, commentId);

        mockMvc.perform(get("/v1/comment/getMetric/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.message").value("Metric got with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber());
    }

    @Test
    public void shouldCreateNewComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        CommentDTO dto = new CommentDTO(
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "user"
        );

        mockMvc.perform(post("/v1/comment/" + postId)
                        .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("Comment created with successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.content").value("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

    }

    @Test
    public void shouldGetTheComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        mockMvc.perform(get("/v1/comment/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment found with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.content").isString());
    }

    @Test
    public void shouldDeleteTheComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentTestUtil.getCommentAndReturnMockResult(mockMvc, token, commentId);

        mockMvc.perform(delete("/v1/comment/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Comment deleted with successfully"));
    }

    @Test
    public void shouldUpdateTheComment() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentTestUtil.getCommentAndReturnMockResult(mockMvc, token, commentId);

        CommentDTO dto = new CommentDTO(
                "UPDATEUPDATEUPDATEUPDATEUPDATEUPDATEUPDATEUPDATEUPDATEUPDATE",
                "user"
        );

        mockMvc.perform(put("/v1/comment/" + commentId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Comment updated with successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.id").value(commentId))
                .andExpect(jsonPath("$.result.content").value(dto.content()));
    }

    @Test
    public void shouldGetAllCommentByUser() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentTestUtil.createMultiComment(mockMvc, objectMapper, token, categoryId, postId, 19);

        mockMvc.perform(get("/v1/comment/getAllByUser?page=0&size=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(20))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void shouldGetAllCommentByPost() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long commentId = CommentTestUtil.createCommentAndReturnCommentId(mockMvc, objectMapper, token, categoryId, postId);

        CommentTestUtil.createMultiComment(mockMvc, objectMapper, token, categoryId, postId, 19);

        mockMvc.perform(get("/v1/comment/GetAllCommentsOfPost/" + postId + "?page=0&size=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(20))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0));
    }

}
