package br.com.Blog.api.integration.utils;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTestUtils {

    public static Map<String, String> createAndLogUserAndReturnTokens(MockMvc mockMvc, ObjectMapper objectMapper) {
        try {
            Random random = new Random();
            UserDTO dto = new UserDTO(
                    "user",
                    "testOfSilva" + random.nextInt(1000) + "@gmail.com",
                    "12345678"
            );

            MvcResult resultRegitser = mockMvc.perform(post("/v1/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result.email").value(dto.email().trim().toLowerCase()))
                    .andReturn();

            String responseCreateUser = resultRegitser.getResponse().getContentAsString();
            JsonNode responseCreateUserJson = objectMapper.readTree(responseCreateUser);
            String userId = responseCreateUserJson.get("result").get("id").asText();

            LoginDTO loginDTO = new LoginDTO(dto.email().trim().toLowerCase(),dto.password());

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
            tokens.put("id", userId);

            return tokens;
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> createAndLogUserAndReturnTokensV2(MockMvc mockMvc, ObjectMapper objectMapper) {
        try {
            Random random = new Random();
            UserDTO dto = new UserDTO(
                    "user",
                    "testOfSilva" + random.nextInt(1000) + "@gmail.com",
                    "12345678"
            );

            MvcResult resultRegitser = mockMvc.perform(post("/v1/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result.email").value(dto.email().trim().toLowerCase()))
                    .andReturn();

            String responseCreateUser = resultRegitser.getResponse().getContentAsString();

            JsonNode responseCreateUserJson = objectMapper.readTree(responseCreateUser);

            String userId = responseCreateUserJson.get("result").get("id").asText();

            LoginDTO loginDTO = new LoginDTO(dto.email().trim().toLowerCase(),dto.password());

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
            tokens.put("id", userId);

            return tokens;
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void createMultiUser(MockMvc mockMvc, ObjectMapper objectMapper, int  amount) {
        try {
            for (int i = 0; i < amount; i++) {
                UserDTO dto = new UserDTO(
                        "user",
                        "testOfSilva" + i + "@gmail.com",
                        "12345678"
                );

                mockMvc.perform(post("/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.result.email").value(dto.email().trim().toLowerCase()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
