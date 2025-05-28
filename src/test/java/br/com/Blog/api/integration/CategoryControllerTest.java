package br.com.Blog.api.integration;

import br.com.Blog.api.DTOs.CategoryDTO;
import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.categoryRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldCreateNewCategory() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com".trim().toLowerCase(),
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        LoginDTO loginDTO = new LoginDTO(
                "testOfSilva@gmail.com".trim().toLowerCase(),
                "12345678"
        );

        MvcResult loginResult = mockMvc.perform(post("/v1/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginJson);
        String token = loginNode.get("token").asText();

        assertNotNull(token);

        CategoryDTO categoryDTO = new CategoryDTO(
                "TI"
        );

        mockMvc.perform(post("/v1/category/")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Category created with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.name").value(categoryDTO.name()));
    }

    @Test
    public void shouldGetTheCategory() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com".trim().toLowerCase(),
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        LoginDTO loginDTO = new LoginDTO(
                "testOfSilva@gmail.com".trim().toLowerCase(),
                "12345678"
        );

        MvcResult loginResult = mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginJson);
        String token = loginNode.get("token").asText();

        assertNotNull(token);

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
        Long categoryId = categoyNode.get("result").get("id").asLong();

        assertNotNull(categoryId);

        mockMvc.perform(get("/v1/category/" + categoryId)
                .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.id").value(categoryId))
                .andExpect(jsonPath("$.message").value("Category found with successfully"))
                .andExpect(jsonPath("$.result.name").isString())
                .andExpect(jsonPath("$.result.name").value(categoryDTO.name()));

    }

    @Test
    public void shouldDeleteTheCategory() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        LoginDTO loginDTO = new LoginDTO(
                "testOfSilva@gmail.com",
                "12345678"
        );

        MvcResult loginResult = mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginJson);
        String token = loginNode.get("token").asText();

        assertNotNull(token);

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
        long categoryId = categoyNode.get("result").get("id").asLong();

        mockMvc.perform(delete("/v1/category/" + categoryId)
                .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.message").value("Task deleted with successfully"));

    }

    @Test
    public void shouldUpdateTheCategory() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        LoginDTO loginDTO = new LoginDTO(
                "testOfSilva@gmail.com",
                "12345678"
        );

        MvcResult loginResult = mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginJson);
        String token = loginNode.get("token").asText();

        assertNotNull(token);

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
        long categoryId = categoyNode.get("result").get("id").asLong();

        CategoryDTO categoryDTOUpdate = new CategoryDTO(
                "T.I"
        );

        mockMvc.perform(put("/v1/category/" + categoryId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTOUpdate)))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("Category update with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.id").value(categoryId))
                .andExpect(jsonPath("$.result.name").value(categoryDTOUpdate.name()));
    }

}
