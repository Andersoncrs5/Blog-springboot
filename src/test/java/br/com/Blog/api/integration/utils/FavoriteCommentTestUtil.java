package br.com.Blog.api.integration.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoriteCommentTestUtil {
    public static void saveCOmmentHowFavorite(MockMvc mockMvc, long commentId, String token) throws Exception {
        mockMvc.perform(post("/v1/favoriteComment/add/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Comment has been favorited successfully!!"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.user").exists())
                .andExpect(jsonPath("$.result.comment").exists());
    }

    public static String saveCommentHowFavoriteAndReturnId(MockMvc mockMvc, long commentId, String token, ObjectMapper objectMapper) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/favoriteComment/add/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Comment has been favorited successfully!!"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.user").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andReturn();

        String postJson = result.getResponse().getContentAsString();
        JsonNode postNode = objectMapper.readTree(postJson);
        return postNode.get("result").get("id").asText();

    }

}
