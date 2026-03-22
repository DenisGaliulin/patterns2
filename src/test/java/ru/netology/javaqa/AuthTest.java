package ru.netology.javaqa;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static ru.netology.javaqa.DataGenerator.Registration.getRegisteredUser;
import static ru.netology.javaqa.DataGenerator.Registration.getUser;
import static ru.netology.javaqa.DataGenerator.getRandomLogin;
import static ru.netology.javaqa.DataGenerator.getRandomPassword;

class AuthTest {

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(io.restassured.filter.log.LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        var registeredUser = getRegisteredUser("active");

        given()
                .spec(requestSpec)
                .body(registeredUser)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        var notRegisteredUser = getUser("active");

        given()
                .spec(requestSpec)
                .body(notRegisteredUser)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = getRegisteredUser("blocked");

        given()
                .spec(requestSpec)
                .body(blockedUser)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = getRegisteredUser("active");
        var wrongLogin = getRandomLogin();

        given()
                .spec(requestSpec)
                .body(registeredUser)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        var userWithWrongLogin = new DataGenerator.RegistrationDto(
                wrongLogin,
                registeredUser.getPassword(),
                "active"
        );

        given()
                .spec(requestSpec)
                .body(userWithWrongLogin)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = getRegisteredUser("active");
        var wrongPassword = getRandomPassword();

        given()
                .spec(requestSpec)
                .body(registeredUser)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        var userWithWrongPassword = new DataGenerator.RegistrationDto(
                registeredUser.getLogin(),
                wrongPassword,
                "active"
        );

        given()
                .spec(requestSpec)
                .body(userWithWrongPassword)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }
}