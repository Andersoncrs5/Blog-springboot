package br.com.Blog.api.integration.utils;

import br.com.Blog.api.entities.enums.LikeOrUnLike;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentLikeTestUtil {

    public static String saveLikeOrDislikeInCommentAndReturnId(MockMvc mockMvc, LikeOrUnLike action, long commentId, String token, ObjectMapper objectMapper) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/commentLike/" + action + "/" + commentId)
                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
                .andReturn();

        String postJson = result.getResponse().getContentAsString();
        JsonNode postNode = objectMapper.readTree(postJson);
        return postNode.get("result").get("id").asText();
    }

}
