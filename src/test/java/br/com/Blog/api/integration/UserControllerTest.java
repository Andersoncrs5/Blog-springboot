package br.com.Blog.api.integration;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldCreateNewUser() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created with successfully"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.email").value(dto.email().trim().toLowerCase()));
    }

    @Test
    public void shouldLogTheUser() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created with successfully"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.email").value(dto.email().trim().toLowerCase()));

        LoginDTO loginDTO = new LoginDTO(
                "testOfSilva@gmail.com".toLowerCase().trim(),
                "12345678"
        );

        mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refresh").exists())
                .andExpect(jsonPath("$.refresh").isString());

    }

    @Test
    public void shouldGetMetricOfUser() throws Exception {
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
                "testOfSilva@gmail.com".toLowerCase().trim(),
                "12345678"
        );

        MvcResult result = mockMvc.perform(post("/v1/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        String token = node.get("token").asText();

        assertNotNull(token);

        mockMvc.perform(get("/v1/user/getMetric")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User metric found with successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));

    }

    @Test
    public void shouldGetTheUser() throws Exception {
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
                "testOfSilva@gmail.com".toLowerCase().trim(),
                "12345678"
        );

        MvcResult result = mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        String token = node.get("token").asText();

        assertNotNull(token);

        mockMvc.perform(get("/v1/user/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User found with successfully"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.result.email").value(loginDTO.email()))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber());


    }

    @Test
    public void shouldGetUserProfileById() throws Exception {
        UserDTO dto = new UserDTO(
                "user",
                "testOfSilva@gmail.com",
                "12345678"
        );

        MvcResult registerResult = mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerJson = registerResult.getResponse().getContentAsString();
        JsonNode registerNode = objectMapper.readTree(registerJson);
        Long userId = registerNode.get("result").get("id").asLong();

        LoginDTO loginDTO = new LoginDTO(
                dto.email().toLowerCase().trim(),
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

        mockMvc.perform(get("/v1/user/getProfile/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User found with successfully"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.result.id").value(userId))
                .andExpect(jsonPath("$.result.email").value(loginDTO.email()))
                .andExpect(jsonPath("$.result.name").value(dto.name()));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        UserDTO userDto = new UserDTO(
                "user",
                "testofsilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register").
                contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO(
                "testofsilva@gmail.com",
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

        mockMvc.perform(get("/v1/user/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value(loginDTO.email()))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber());

        mockMvc.perform(delete("/v1/user/")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("User deleted with successfully"));

    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO(
                "user",
                "testofsilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO(
                "testofsilva@gmail.com",
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

        UserDTO userDTOToUpdate = new UserDTO(
                "user update",
                "testofsilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(put("/v1/user/")
                        .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTOToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User update with successfully"))
                .andExpect(jsonPath("$.result.name").value("user update"));

    }

    @Test
    public void shouldMakeLogout() throws Exception {
        UserDTO userDTO = new UserDTO(
                "user",
                "testofsilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO(
                "testofsilva@gmail.com",
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

        mockMvc.perform(get("/v1/user/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout make with successfully"));
    }

    @Test
    public void shouldMakeRefresh() throws Exception {
        UserDTO userDTO = new UserDTO(
                "user",
                "testofsilva@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO(
                "testofsilva@gmail.com",
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
        String refresh = loginNode.get("refresh").asText();

        assertNotNull(token);

        mockMvc.perform(post("/v1/user/refresh/" + refresh )
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refresh").exists())
                .andExpect(jsonPath("$.refresh").isString());

    }

}