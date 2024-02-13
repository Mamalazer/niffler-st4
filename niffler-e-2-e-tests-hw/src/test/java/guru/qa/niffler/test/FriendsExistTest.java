package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

public class FriendsExistTest extends BaseWebTest{

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();

    @BeforeEach
    void doLogin() {
        Selenide.open(Config.getInstance().frontUrl());
        welcomePage.goToLoginPage();
    }

    @Test
    @DisplayName("У пользователя на странице со списком друзей отображается друг")
    void userHasFriendInFriendsPage(@User(WITH_FRIENDS) UserJson user) {
        loginPage.doSuccessfulLogin(user.username(), user.testData().password())
                .header.goToFriendsPage()
                .checkThatUserIsFriend("elephant");
    }

    @Test
    @DisplayName("У пользователя на странице со списком всех пользователей отображается друг")
    void userHasFriendInAllPeoplePage(@User(WITH_FRIENDS) UserJson user) {
        loginPage.doSuccessfulLogin(user.username(), user.testData().password())
                .header.goToAllPeoplePage()
                .checkThatUserIsFriend("dima");
    }
}
