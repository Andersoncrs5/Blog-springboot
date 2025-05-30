package br.com.Blog.api.integration.utils;

import br.com.Blog.api.DTOs.PostDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostTestUtil {
    public static long createPostAndReturnId(MockMvc mockMvc, ObjectMapper objectMapper, Long categoryId, String token) throws Exception {
        Random random = new Random();
        PostDTO postDTO = new PostDTO(
                "Post testtesttesttest",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                9,
                "",
                "18443" + random.nextInt(10000)
        );

        MvcResult resultPost = mockMvc.perform(post("/v1/posts/" + categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Post created with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.title").value(postDTO.title()))
                .andExpect(jsonPath("$.result.content").value(postDTO.content()))
                .andReturn();

        String postJson = resultPost.getResponse().getContentAsString();
        JsonNode postNode = objectMapper.readTree(postJson);
        return postNode.get("result").get("id").asLong();
    }

    public static MvcResult getPost(MockMvc mockMvc, Long postId, String token) throws Exception {
        return mockMvc.perform(get("/v1/posts/"+ postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Post found with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andReturn();
    }

    public static void createMultiPost(MockMvc mockMvc, long categoryId, String token, ObjectMapper objectMapper , int amount) throws Exception {
        for (int i = 1; i < amount; i++) {
            PostDTO postDTO = new PostDTO(
                    "Post testtesttesttest" + i,
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + i,
                    9,
                    "",
                    "1" + i
            );

            mockMvc.perform(post("/v1/posts/" + categoryId)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Post created with successfully"))
                    .andExpect(jsonPath("$.result.id").exists())
                    .andExpect(jsonPath("$.result.id").isNumber())
                    .andExpect(jsonPath("$.result.title").value(postDTO.title()))
                    .andExpect(jsonPath("$.result.content").value(postDTO.content()))
                    .andReturn();
        }
    }

}
