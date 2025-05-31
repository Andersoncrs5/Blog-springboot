package br.com.Blog.api.integration;

import br.com.Blog.api.integration.utils.FollowersTestUtil;
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
public class FollowersControllerTest {

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
    public void shouldFollowTheUser() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        Map<String, String> tokens1 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token1 = tokens1.get("token");
        String userId1 = tokens1.get("id");

        mockMvc.perform(post("/v1/followers/follow/" + userId1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("You are follower this the user"));
    }

    @Test
    public void shouldUnfollowTheUser() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        Map<String, String> tokens1 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token1 = tokens1.get("token");
        String userId1 = tokens1.get("id");

        FollowersTestUtil.followeringUser(mockMvc, userId1, token);

        mockMvc.perform(post("/v1/followers/unfollow/" + userId1)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("You have unfollowed user"));
    }

    @Test
    public void shouldAreFollowing() throws Exception {
        Map<String, String> tokens = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token = tokens.get("token");

        Map<String, String> tokens1 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token1 = tokens1.get("token");
        String userId1 = tokens1.get("id");

        Map<String, String> tokens2 = UserTestUtils.createAndLogUserAndReturnTokens(mockMvc, objectMapper);
        String token2 = tokens2.get("token");
        String userId2 = tokens2.get("id");

        FollowersTestUtil.followeringUser(mockMvc, userId1, token);

        mockMvc.perform(post("/v1/followers/areFollowing/" + userId1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").isBoolean())
                .andExpect(jsonPath("$.result").value(true));

//        FollowersTestUtil.unfolloweringUser(mockMvc, userId1, token);

        mockMvc.perform(post("/v1/followers/areFollowing/" + userId2)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").isBoolean())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    public void shouldgetAllFollowed() throws Exception {
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

        mockMvc.perform(get("/v1/followers/?page=0&size=10")
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

}
