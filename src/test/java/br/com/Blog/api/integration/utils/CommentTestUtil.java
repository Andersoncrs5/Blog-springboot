package br.com.Blog.api.integration.utils;

import br.com.Blog.api.DTOs.CommentDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentTestUtil {
    public static MvcResult createCommentAndReturnMvcResult(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token,
            long categoryId,
            long postId
    ) throws Exception {
        CommentDTO dto = new CommentDTO(
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "user"
        );
        return mockMvc.perform(post("/v1/comment/" + postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("Comment created with successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.content").value("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
                .andReturn();

    }


    public static long createCommentAndReturnCommentId(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token,
            long categoryId,
            long postId
    ) throws Exception {
        CommentDTO dto = new CommentDTO(
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "user"
        );

        MvcResult result = mockMvc.perform(post("/v1/comment/" + postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("Comment created with successfully"))
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.content").value("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
                .andReturn();

        String postJson = result.getResponse().getContentAsString();
        JsonNode postNode = objectMapper.readTree(postJson);
        return postNode.get("result").get("id").asLong();
    }


    public static MvcResult getCommentAndReturnMockResult(
            MockMvc mockMvc,
            String token,
            long commentId
    ) throws Exception {
        return mockMvc.perform(get("/v1/comment/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment found with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.content").isString())
                .andReturn();
    }

    public static void createMultiComment(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token,
            long categoryId,
            long postId,
            int amount
    ) throws Exception {
        for (int i = 0; i < amount; i++) {
            CommentDTO dto = new CommentDTO(
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + i,
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
                    .andExpect(jsonPath("$.result.content").value("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + i));
        }
    }

}
