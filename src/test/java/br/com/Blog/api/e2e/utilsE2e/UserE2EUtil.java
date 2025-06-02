package br.com.Blog.api.e2e.utilsE2e;

import io.restassured.common.mapper.TypeRef;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserE2EUtil {
    public static void createUser(int port) {
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

    public static String createUserLogAndReturnToken(int port) {
        UserE2EUtil.createUser(port);

        String dto = """
                {
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        return given().port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .post("/v1/user/login")
                .then()
                .statusCode(200)
                .body("token", isA(String.class))
                .body("refresh", isA(String.class))
                .extract()
                .path("token").toString();
    }

    public static String createUserLogAndReturnRefreshToken(int port) {
        UserE2EUtil.createUser(port);

        String dto = """
                {
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        return given().port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .post("/v1/user/login")
                .then()
                .statusCode(200)
                .body("token", isA(String.class))
                .body("refresh", isA(String.class))
                .extract()
                .path("refresh").toString();
    }

    public static Map<String, String> createUserLogAndReturnTokens(int port) {
        UserE2EUtil.createUser(port);

        String dto = """
            {
                "email":"test@gmail.com",
                "password":"12345678"
            }
            """;

        return given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(dto)
                .post("/v1/user/login")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<Map<String, String>>() {});
    }

}
