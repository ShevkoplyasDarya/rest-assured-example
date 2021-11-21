import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemoWebshopTests {
    @Test
    void addToWishListTestWithCookie() {
        step("Get cookie and set it to browser by API", () -> {
            String authorizationCookie = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("Email", "dora@test.com")
                    .formParam("Password", "123456")
                    .when()
                    .post("http://demowebshop.tricentis.com/login")
                    .then()
                    .statusCode(302)
                    .extract()
                    .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("http://demowebshop.tricentis.com/Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));

            step("Open main page", () ->
                    open("http://demowebshop.tricentis.com"));

            step("Check displayed login", () ->
                    $(".account").shouldHave(text("dora@test.com")));

        });

        step("Add item to Wishlist", () -> {
            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .when()
                    .body("product_attribute_28_7_10=25&product_attribute_28_1_11=29&addtocart_28.EnteredQuantity=1")
                    .cookie("Nop.customer=f0ecbd0e-bd35-44f6-b7b7-000f997a67b2;")
                    .post("http://demowebshop.tricentis.com/addproducttocart/details/28/2")
                    .then()
                    .statusCode(200)
                    .body("success", is(true))
                    .body("message", is("The product has been added to your <a href=" +
                            "\"/wishlist\">wishlist</a>"));
        });

        step("Check Wishlist page", () -> {
            Response response =
                    given()
                            .get("http://demowebshop.tricentis.com/wishlist")
                            .then()
                            .statusCode(200)
                            .extract().response();
            System.out.println("Response: " + response.toString());
        });

        step("Check added item by UI", () -> {
            open("http://demowebshop.tricentis.com/wishlist");
            $(".product > a").shouldHave(text("Blue and green Sneaker"));

        });
    }

}
