package br.com.Blog.api.integration;

import br.com.Blog.api.integration.utils.CategoryTestUtils;
import br.com.Blog.api.integration.utils.FavoritePostTestUtil;
import br.com.Blog.api.integration.utils.PostTestUtil;
import br.com.Blog.api.integration.utils.UserTestUtils;
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
public class FavoritePostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        unit.favoritePostRepository.deleteAll();
        unit.postRepository.deleteAll();
        unit.categoryRepository.deleteAll();
        unit.userRepository.deleteAll();
    }

    @Test
    public void shouldDeleteFavoritePost() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId1 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        String id = FavoritePostTestUtil.savePostHowFavoriteAndReturnId(mockMvc, postId, token, objectMapper);

        mockMvc.perform(delete("/v1/favoritePost/" + id)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post removed with favorite!"));
    }

    @Test
    public void shouldExistsFavorite() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId1 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId, token);

        mockMvc.perform(get("/v1/favoritePost/exists/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").isBoolean())
                .andExpect(jsonPath("result").value(true));

        mockMvc.perform(get("/v1/favoritePost/exists/" + postId1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").isBoolean())
                .andExpect(jsonPath("result").value(false));
    }

    @Test
    public void shouldFavoritePost() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        mockMvc.perform(post("/v1/favoritePost/add/" + postId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Post has been favorited successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.user").exists())
                .andExpect(jsonPath("$.result.post").exists());
    }

    @Test
    public void shouldGetAllFavoritePost() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId1 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId2 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId3 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId4 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId5 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId6 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId7 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId1, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId2, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId3, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId4, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId5, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId6, token);
        FavoritePostTestUtil.savePostHowFavorite(mockMvc, postId7, token);

        mockMvc.perform(get("/v1/favoritePost/GetAllFavoritePostOfUser")
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
