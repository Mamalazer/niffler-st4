package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.Point.OUTER;

public class ProfileTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();

    @BeforeEach
    void doLogin() {
    Selenide.open(Config.getInstance().frontUrl());
        welcomePage.goToLoginPage();
    }

    @TestUser()
    @DisplayName("Смена имени пользователя")
    @Test
    void changeUserName(@User(OUTER) UserJson userJson) {
        loginPage.doSuccessfulLogin(userJson.username(), userJson.testData().password())
                .header.goToProfilePage()
                .setFirstname(userJson.username())
                .submitData()
                .checkFirstname(userJson.username());
    }

    @TestUser()
    @DisplayName("Добавление аватара")
    @Test
    void setUserAvatar(@User(OUTER) UserJson userJson) {
        String avatarPath = "test-data/avatar.jpeg";

        loginPage.doSuccessfulLogin(userJson.username(), userJson.testData().password())
                .header.goToProfilePage()
                .uploadAvatarFromClasspath(avatarPath)
                .submitData()
                .header.checkAvatar(avatarPath);
    }
}
