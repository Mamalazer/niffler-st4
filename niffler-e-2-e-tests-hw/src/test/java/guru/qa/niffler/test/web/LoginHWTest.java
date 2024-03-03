package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginHWTest extends BaseWebTest {

  WelcomePage welcomePage = new WelcomePage();
  LoginPage loginPage = new LoginPage();

  @BeforeEach
  void doLogin() {
    Selenide.open(Config.getInstance().frontUrl());
    welcomePage.goToLoginPage();
  }

  @DbUser(username = "dog", password = "12345")
  @DisplayName("Успешный логин")
  @Test
  void successfulLogin(UserAuthEntity userAuth) {
    loginPage.doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword())
            .checkIsLoaded();
  }

  @DbUser()
  @DisplayName("Успешный логин с помощью создания случайного пользователя")
  @Test
  void successfulLoginWithRandomUser(UserAuthEntity userAuth) {
    loginPage.doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword())
            .checkIsLoaded();
  }

  @DbUser()
  @Test
  @DisplayName("Неуспешный логин. Введён неверный пароль")
  void unsuccessfulLoginWithAnotherPassword(UserAuthEntity userAuth) {
    loginPage.doFailedLogin(userAuth.getUsername(), "123")
            .checkError();
  }

  @DbUser()
  @Test
  @DisplayName("Неуспешный логин. Введено неверное имя пользователя")
  void unsuccessfulLoginWithAnotherUserName(UserAuthEntity userAuth) {
    loginPage.doFailedLogin("firefly", userAuth.getPassword())
            .checkError();
  }
}
