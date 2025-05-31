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
public class UserMetricControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.favoriteCommentRepository.deleteAll();
        this.unit.commentRepository.deleteAll();
        this.unit.favoritePostRepository.deleteAll();
        this.unit.postRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldCountFollowersCount() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        Map<String, String> tokens1 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String userId1 = tokens1.get("id");

        Map<String, String> tokens2 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String userId2 = tokens2.get("id");

        Map<String, String> tokens3 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String userId3 = tokens3.get("id");

        Map<String, String> tokens4 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String userId4 = tokens4.get("id");

        Map<String, String> tokens5 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String userId5 = tokens5.get("id");

        FollowersTestUtil.followeringUser(mockMvc, userId1, token);
        FollowersTestUtil.followeringUser(mockMvc, userId2, token);
        FollowersTestUtil.followeringUser(mockMvc, userId3, token);
        FollowersTestUtil.followeringUser(mockMvc, userId4, token);
        FollowersTestUtil.followeringUser(mockMvc, userId5, token);

        mockMvc.perform(get("/v1/user/getMetric")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.followersCount").isNumber())
                .andExpect(jsonPath("$.result.followingCount").value(5));
    }

    @Test
    public void shouldPostsCount() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId1 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId2 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId3 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId4 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId5 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        mockMvc.perform(get("/v1/user/getMetric")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postsCount").value(6));
    }

    @Test
    public void shouldCommentsCount() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        CommentTestUtil.createMultiComment(mockMvc, objectMapper, token, categoryId, postId, 20);

        mockMvc.perform(get("/v1/user/getMetric")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.commentsCount").value(20));
    }

    @Test
    public void shouldLikesAndDislikeCount() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);

        String token = tokens.get("token");

        long categoryId = CategoryTestUtils.createCategoryAndReturnId(mockMvc, objectMapper, token);
        long postId = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);
        long postId2 = PostTestUtil.createPostAndReturnId(mockMvc, objectMapper, categoryId, token);

        mockMvc.perform(post("/v1/postLike/" + LikeOrUnLike.LIKE + "/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reaction added successfully"));

        mockMvc.perform(post("/v1/postLike/" + LikeOrUnLike.UNLIKE + "/" + postId2)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reaction added successfully"));

        mockMvc.perform(get("/v1/user/getMetric")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.likesGivenCount").value(1))
                .andExpect(jsonPath("$.result.likesGivenCountCreateByDay").value(1))
                .andExpect(jsonPath("$.result.deslikesGivenCount").value(1))
                .andExpect(jsonPath("$.result.deslikesGivenCountCreateByDay").value(1));
    }

    @Test
    public void shouldFavoritePostCount() throws Exception {
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

        mockMvc.perform(get("/v1/user/getMetric")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.savedPostsCount").value(8));
    }

    @Test
    public void shouldFavoriteCommentCount() throws Exception {
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

        mockMvc.perform(get("/v1/user/getMetric")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.savedCommentsCount").value(8));
    }

}
