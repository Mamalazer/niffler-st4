package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();

    @BeforeEach
    void doLogin() {
    Selenide.open(Config.getInstance().frontUrl());
        welcomePage.goToLoginPage();
    }

    @DbUser()
    @DisplayName("Смена имени пользователя")
    @Test
    void changeUserName(UserAuthEntity userAuth) {
        loginPage.doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword())
                .header.goToProfilePage()
                .setFirstname(userAuth.getUsername())
                .submitData()
                .checkFirstname(userAuth.getUsername());
    }

    @DbUser()
    @DisplayName("Добавление аватара")
    @Test
    void setUserAvatar(UserAuthEntity userAuth) {
        loginPage.doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword())
                .header.goToProfilePage()
                .uploadAvatarFromClasspath("test-data/avatar.jpeg");
    }
}
