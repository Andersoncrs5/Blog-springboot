package br.com.Blog.api.e2e;

import br.com.Blog.api.e2e.utilsE2e.CategoryE2EUtil;
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
public class CategoryTestE {

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
        this.unit.categoryRepository.deleteAll();
        this.unit.categoryRepository.deleteAll();
        this.unit.userRepository.deleteAll();
    }

    @Test
    public void testGetCategory() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);
        Integer categoryId = CategoryE2EUtil.createCategoryAndReturnId(port, token);

        given()
                .port(port)
                .when()
                .delete("/v1/category/" + 0)
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/v1/category/" + 0)
                .then()
                .statusCode(400);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/v1/category/" + -1)
                .then()
                .statusCode(400);


        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/category/" + categoryId)
                .then()
                .statusCode(200)
                .body("message", equalTo("Category found with successfully"))
                .body("result", notNullValue());
    }

    @Test
    public void testCreateCategory() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);

        String dto = """
                {
                    "name":"TI"
                }
                """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .when()
                .post("/v1/category/")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/v1/category/")
                .then()
                .statusCode(201)
                .body("message", equalTo("Category created with successfully"))
                .body("result", notNullValue());
    }

    @Test
    public void testGetAllCategory() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);
        CategoryE2EUtil.createCategoryAndReturnId(port, token);

        given()
                .port(port)
                .when()
                .get("/v1/category")
                .then()
                .statusCode(403);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/category")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteCategory() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);
        Integer categoryId = CategoryE2EUtil.createCategoryAndReturnId(port, token);

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/v1/category/" + categoryId)
                .then()
                .statusCode(200)
                .body("message", equalTo("Task deleted with successfully"));
    }

    @Test
    public void testUpdateCategory() {
        String token = UserE2EUtil.createUserLogAndReturnToken(port);
        Integer categoryId = CategoryE2EUtil.createCategoryAndReturnId(port, token);

        String dto = """
                {
                    "name":"TIS"
                }
                """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .when()
                .put("/v1/category/" + categoryId)
                .then()
                .statusCode(403);

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/v1/category/" + categoryId)
                .then()
                .statusCode(200)
                .body("message", equalTo("Category update with successfully"));
    }

}
