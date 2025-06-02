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

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTestE {

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
        this.unit.commentLikeRepository.deleteAll();
        this.unit.favoriteCommentRepository.deleteAll();
        this.unit.commentRepository.deleteAll();
        this.unit.postLikeRepository.deleteAll();
        this.unit.postRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        String dto = """
                {
                    "name":"test of silva",
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        given().port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .when()
                .post("/v1/user/register")
                .then()
                .statusCode(201)
                .body("message", equalTo("User created with successfully"))
                .body("result", notNullValue())
                .body("result.email", equalTo("test@gmail.com"));
    }

    @Test
    public void testLoginUser() {
        UserE2EUtil.createUser(port);

        String dto = """
                {
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        given().port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .post("/v1/user/login")
                .then()
                .statusCode(200)
                .body("token", isA(String.class))
                .body("refresh", isA(String.class));
    }

    @Test
    public void testDeleteUser() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        given()
                .port(port)
                .when()
                .delete("/v1/user/")
                .then()
                .statusCode(403);

        given().port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/v1/user/")
                .then()
                .statusCode(200)
                .body("message", equalTo("User deleted with successfully"));
    }

    @Test
    public void testGetMetricUser() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        given()
                .port(port)
                .when()
                .get("/v1/user/getMetric")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/user/getMetric")
                .then()
                .statusCode(200)
                .body("message", notNullValue())
                .body("message", equalTo("User metric found with successfully"));
    }

    @Test
    public void testGetMetricOfAnotherUser() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        given()
                .port(port)
                .when()
                .get("/v1/user/me")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/user/me")
                .then()
                .statusCode(200)
                .body("message", equalTo("User found with successfully"))
                .body("result", notNullValue());
    }

    @Test
    public void testListPostsOfUser() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        given()
                .port(port)
                .when()
                .get("/v1/user/ListPostsOfUser")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/user/ListPostsOfUser")
                .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateUser() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        given()
                .port(port)
                .when()
                .put("/v1/user/")
                .then()
                .statusCode(403);

        String dto = """
                {
                    "name":"test of silva update",
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .when()
                .put("/v1/user/")
                .then()
                .statusCode(200)
                .body("message", equalTo("User update with successfully"))
                .body("result", notNullValue());

    }

    @Test
    public void testLogout() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        given()
                .port(port)
                .when()
                .get("/v1/user/logout")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/user/logout")
                .then()
                .statusCode(200)
                .body("message", equalTo("Logout make with successfully"))
                .body("result", notNullValue());
    }

    @Test
    public void testRefresh() {
        Map<String, String> tokens = UserE2EUtil.createUserLogAndReturnTokens(port);
        String token = tokens.get("token");
        String refresh = tokens.get("refresh");

        given()
                .port(port)
                .when()
                .post("/v1/user/refresh/")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/v1/user/refresh/" + refresh)
                .then()
                .statusCode(200);
    }

}
