package br.com.Blog.api.e2e;

import br.com.Blog.api.e2e.utilsE2e.UserE2EUtil;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostTestE {

    @LocalServerPort
    private int port;

    @Autowired
    private UnitOfWorkRepository unit;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://127.0.0.1";
    }

    @BeforeEach
    void clearRepository() {
        this.unit.postRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void testGetPost() {

    }

    @Test
    public void testCreatePost() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        String dto = """
                {
                    "title": "post 11111111",                 
                    "content": "contentcontentcontentcontentcontentcontentcontentcontent",                 
                    "readingTime": 9,
                    "slug": "123456"                 
                }
                """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .when()
                .post("/v1/posts/")
                .then();


    }

}
