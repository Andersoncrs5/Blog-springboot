package br.com.Blog.api.integration;

import br.com.Blog.api.DTOs.CategoryDTO;
import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.PostDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeEach
    void setup() {
        this.unit.categoryRepository.deleteAll();
        this.unit.postRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void shouldGetMetricThePost() throws Exception {
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

        PostDTO postDTO = new PostDTO(
                "Post testtesttesttest",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                9,
                "",
                "1844380718453"
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
        long postId = postNode.get("result").get("id").asLong();

        mockMvc.perform(get("/v1/posts/getMetric/" + postId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post metric found with successfully"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").isNumber());
    }

    @Test
    public void shouldUpdateThePost() throws Exception {
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

        PostDTO postDTO = new PostDTO(
                "Post testtesttesttest",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                9,
                "",
                "1844380718453"
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
        Long postId = postNode.get("result").get("id").asLong();

        mockMvc.perform(get("/v1/posts/"+ postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Post found with successfully"))
                .andExpect(jsonPath("$.result.id").exists());

        PostDTO postDTOUpdate = new PostDTO(
                "Post updateupdateupdate",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                4,
                "",
                "1844380718453"
        );

        mockMvc.perform(put("/v1/posts/" + postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTOUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Post updated with successfully"))
                .andExpect(jsonPath("$.result.id").value(postId))
                .andExpect(jsonPath("$.result.title").value(postDTOUpdate.title()))
                .andExpect(jsonPath("$.result.readingTime").value(postDTOUpdate.readingTime()));
    }

    @Test
    public void shouldCreatePost() throws Exception {
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

        PostDTO postDTO = new PostDTO(
                "Post testtesttesttest",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                9,
                "",
                "1844380718453"
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
                .andExpect(jsonPath("$.result.content").value(postDTO.content()));
    }

    @Test
    public void shouldGetThePost() throws Exception {
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

        PostDTO postDTO = new PostDTO(
                "Post testtesttesttest",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                9,
                "",
                "1844380718453"
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
        long postId = postNode.get("result").get("id").asLong();

        mockMvc.perform(get("/v1/posts/"+ postId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Post found with successfully"))
                .andExpect(jsonPath("$.result.id").exists());
    }

    @Test
    public void shouldDeleteThePost() throws Exception {
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

        PostDTO postDTO = new PostDTO(
                "Post testtesttesttest",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                9,
                "",
                "1844380718453"
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
        long postId = postNode.get("result").get("id").asLong();

        mockMvc.perform(get("/v1/posts/"+ postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Post found with successfully"))
                .andExpect(jsonPath("$.result.id").exists());

        mockMvc.perform(delete("/v1/posts/" + postId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post deleted with successfully"));

    }

}