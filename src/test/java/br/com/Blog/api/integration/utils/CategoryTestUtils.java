package br.com.Blog.api.integration.utils;

import br.com.Blog.api.DTOs.CategoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryTestUtils {
    public static MvcResult createCategory(MockMvc mockMvc, ObjectMapper objectMapper, String token) throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO(
                "TI"
        );

        return mockMvc.perform(post("/v1/category/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Category created with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.name").value(categoryDTO.name()))
                .andReturn();
    }

    public static long createCategoryAndReturnId(MockMvc mockMvc, ObjectMapper objectMapper, String token) throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO(
                "TI"
        );

        MvcResult categoryResult = mockMvc.perform(post("/v1/category/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Category created with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.name").value(categoryDTO.name()))
                .andReturn();

        String categoryJson = categoryResult.getResponse().getContentAsString();
        JsonNode categoyNode = objectMapper.readTree(categoryJson);
        long id = categoyNode.get("result").get("id").asLong();


        return id;
    }

}
