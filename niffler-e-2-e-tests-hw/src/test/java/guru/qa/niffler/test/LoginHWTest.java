package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.models.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
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
  @Test
  void successfulLogin(UserAuthEntity userAuth) {
    loginPage.doLogin(userAuth.getUsername(), userAuth.getPassword())
            .checkIsLoaded();
  }

  @DbUser()
  @Test
  void successfulLoginWithRandomUser(UserAuthEntity userAuth) {
    loginPage.doLogin(userAuth.getUsername(), userAuth.getPassword())
            .checkIsLoaded();
  }
}
