package br.com.Blog.api.integration.utils;

import br.com.Blog.api.entities.enums.LikeOrUnLike;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostLikeTestUtil {
    public static void createPostLikeOrDislike(MockMvc mockMvc, long postId, String token, LikeOrUnLike action) throws Exception {
        mockMvc.perform(post("/v1/postLike/" + action + "/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reaction added successfully"));
    }

    public static long createPostLikeOrDislikeAndReturnId(MockMvc mockMvc, long postId, String token, LikeOrUnLike action, ObjectMapper objectMapper) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/postLike/" + action + "/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reaction added successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        JsonNode iDoNotNow = objectMapper.readTree(resultJson);
        return iDoNotNow.get("result").get("id").asLong();
    }
}
