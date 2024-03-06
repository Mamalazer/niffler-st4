package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApiLoginTest extends BaseWebTest {

    MainPage mainPage = new MainPage();

    @BeforeEach
    void doLogin() {
        Selenide.open(Config.getInstance().frontUrl());
    }

    @ApiLogin(user = @DbUser(username = "dog", password = "12345", isRunnable = true))
    @DisplayName("Успешный логин 1")
    @Test
    void successfulApiLogin() {
        mainPage.checkIsLoaded();
    }

    @ApiLogin(user = @DbUser(isRunnable = true))
    @DisplayName("Успешный логин 2")
    @Test
    void successfulApiLogin02() {
        mainPage.checkIsLoaded();
    }

    @ApiLogin(username = "bee", password = "12345")
    @DisplayName("Успешный логин 3")
    @Test
    void successfulApiLogin03() {
        mainPage.checkIsLoaded();
    }
}
