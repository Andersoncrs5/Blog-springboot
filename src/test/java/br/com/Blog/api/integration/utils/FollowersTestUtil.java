package br.com.Blog.api.integration.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FollowersTestUtil {
    public static void followeringUser(MockMvc mockMvc, String userId, String token) throws Exception {
        mockMvc.perform(post("/v1/followers/follow/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("You are follower this the user"));
    }

    public static void unfolloweringUser(MockMvc mockMvc, String userId, String token) throws Exception {
        mockMvc.perform(post("/v1/followers/unfollow/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("You have unfollowed user"));
    }


}
