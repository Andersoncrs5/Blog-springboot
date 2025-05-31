package br.com.Blog.api.integration.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoritePostTestUtil {
    public static void savePostHowFavorite(MockMvc mockMvc, long postId, String token) throws Exception {
        mockMvc.perform(post("/v1/favoritePost/add/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Post has been favorited successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.user").exists())
                .andExpect(jsonPath("$.result.post").exists());
    }

    public static String savePostHowFavoriteAndReturnId(MockMvc mockMvc, long postId, String token, ObjectMapper objectMapper) throws Exception {
        var result = mockMvc.perform(post("/v1/favoritePost/add/" + postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Post has been favorited successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.user").exists())
                .andExpect(jsonPath("$.result.post").exists())
                .andReturn();

        String postJson = result.getResponse().getContentAsString();
        JsonNode postNode = objectMapper.readTree(postJson);
        return postNode.get("result").get("id").asText();
    }

}
