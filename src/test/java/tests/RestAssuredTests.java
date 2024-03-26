package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.lombok.UserDataRegisterLombokModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static specs.UserSpec.*;
import models.lombok.UserDataLombokModel;


public class RestAssuredTests {

    @BeforeAll
    static void restAssuredBase() {
        RestAssured.baseURI = "https://reqres.in/";
        RestAssured.basePath = "api/";
    }

    @Test
    void getSingleResourceWithSchemaTest() {
        Response response = step("User request", ()->
                given()
                .filter(new AllureRestAssured())
                .spec(userSimpleRequestSpec)

                .when()
                .get("unknown/2")

                .then()
                .spec(userResponse200Spec)

                .body(matchesJsonSchemaInClasspath("schemas/single-resource-schema.json"))
                .extract().response());

        step("Check response id", ()-> assertThat(response.path("data.id"), is(2)));
        step("Check response name", ()-> assertThat(response.path("data.name"), is("fuchsia rose")));
        step("Check response year", ()-> assertThat(response.path("data.year"), is(2001)));
        step("Check response color", ()-> assertThat(response.path("data.color"), is("#C74375")));
        step("Check response pantone_value", ()-> assertThat(response.path("data.pantone_value"), is("17-2031")));
        step("Check response url", ()-> assertThat(response.path("support.url"), is("https://reqres.in/#support-heading")));
        step("Check response text", ()-> assertThat(response.path("support.text"), is("To keep ReqRes free, contributions towards server costs are appreciated!")));

    }

    @Test
    void postCreateUserWithSchemaTest() {
        UserDataLombokModel userData = new UserDataLombokModel();
        userData.setName("morpheus");
        userData.setJob("leader");

        Response response = step("User request", ()->
                given()
                        .filter(new AllureRestAssured())
                .spec(userRequestSpec)
                .body(userData)

                .when()
                .post("users")

                .then()
                .spec(userResponse201Spec)

                .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                .extract().response());

        step("Check response name", ()-> assertThat(response.path("name"), is("morpheus")));
        step("Check response job", ()-> assertThat(response.path("job"), is("leader")));
    }

    @Test
    void putUpdateUserWithSchemaTest() {
        UserDataLombokModel userData = new UserDataLombokModel();
        userData.setName("morpheus");
        userData.setJob("zion resident");

        Response response = step("User request", ()->
                given()
                        .filter(new AllureRestAssured())
                .spec(userRequestSpec)
                .body(userData)

                .when()
                .put("users/2")

                .then()
                .spec(userResponse200Spec)

                .body(matchesJsonSchemaInClasspath("schemas/put-user-schema.json"))
                .extract().response());

        step("Check response name", ()-> assertThat(response.path("name"), is("morpheus")));
        step("Check response job", ()-> assertThat(response.path("job"), is("zion resident")));
    }

    @Test
    void patchUpdateUserWithSchemaTest() {
        UserDataLombokModel userData = new UserDataLombokModel();
        userData.setName("morpheus");
        userData.setJob("zion resident");

        Response response = step("User request", ()->
                given()
                        .filter(new AllureRestAssured())
                .spec(userRequestSpec)
                .body(userData)

                .when()
                .patch("users/2")

                .then()
                .spec(userResponse200Spec)

                .body(matchesJsonSchemaInClasspath("schemas/patch-user-schema.json"))
                .extract().response());

        step("Check response name", ()-> assertThat(response.path("name"), is("morpheus")));
        step("Check response job", ()-> assertThat(response.path("job"), is("zion resident")));
    }

    @Test
    void deleteUserWithSchemaTest() {
       step("User request", ()->
                 given()
                         .filter(new AllureRestAssured())
                .spec(userSimpleRequestSpec)

                .when()
                .delete("users/2")

                .then()
                .spec(userResponse204Spec)

                .extract().response());
    }

    @Test
    void postRegisterUnsuccessfulWithSchemaTest() {
        UserDataRegisterLombokModel userRegisterData = new UserDataRegisterLombokModel();
        userRegisterData.setEmail("sydney@fife");

        Response response = step("User request", ()->
                given()
                        .filter(new AllureRestAssured())
                .spec(userRequestSpec)
                .body(userRegisterData)

                .when()
                .post("register")

                .then()
                .spec(userResponse400Spec)

                .body(matchesJsonSchemaInClasspath("schemas/register-unsuccessful-schema.json"))
                .extract().response());

        step("Check response error", ()-> assertThat(response.path("error"), is("Missing password")));
    }
}
