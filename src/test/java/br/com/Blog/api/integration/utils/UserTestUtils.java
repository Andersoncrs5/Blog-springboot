package br.com.Blog.api.integration.utils;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTestUtils {

    public static Map<String, String> createAndLogUserAndReturnTokens(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.email").value(dto.email().trim().toLowerCase()));

        LoginDTO loginDTO = new LoginDTO(
                dto.email().trim().toLowerCase(),
                dto.password()
        );

        MvcResult loginResult = mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", node.get("token").asText());
        tokens.put("refresh", node.get("refresh").asText());

        return tokens;
    }

    public static MvcResult getUserAndReturnMvc(MockMvc mockMvc, String token) throws Exception {
        return mockMvc.perform(get("/v1/user/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andReturn();
    }

}
