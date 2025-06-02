package br.com.Blog.api.e2e.utilsE2e;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CategoryE2EUtil {
    public static Integer createCategoryAndReturnId(int port, String token) {
        String dto = """
                {
                    "name":"TI"
                }
                """;

        return given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/v1/category/")
                .then()
                .statusCode(201)
                .body("message", equalTo("Category created with successfully"))
                .body("result", notNullValue())
                .extract()
                .path("result.id");
    }
}
