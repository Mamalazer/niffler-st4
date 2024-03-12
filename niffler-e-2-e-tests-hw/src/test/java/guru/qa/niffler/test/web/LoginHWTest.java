package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.currency.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.Point.OUTER;

public class LoginHWTest extends BaseWebTest {

  WelcomePage welcomePage = new WelcomePage();
  LoginPage loginPage = new LoginPage();

  @BeforeEach
  void doLogin() {
    Selenide.open(Config.getInstance().frontUrl());
    welcomePage.goToLoginPage();
  }

  @TestUser(
          username = "dog", password = "12345",
          category = @Category(category = "Развлечения", username = "dog"),
          spend = @Spend(
                  username = "dog",
                  description = "Кино",
                  amount = 72500.00,
                  category = "Развлечения",
                  currency = CurrencyValues.RUB,
                  spendDate = "2024-03-12"
          )
  )
  @DisplayName("Успешный логин")
  @Test
  void successfulLogin(@User(OUTER) UserJson userJson) {
    loginPage.doSuccessfulLogin(userJson.username(), userJson.testData().password())
            .checkIsLoaded();
  }

  @TestUser()
  @DisplayName("Успешный логин с помощью создания случайного пользователя")
  @Test
  void successfulLoginWithRandomUser(@User(OUTER) UserJson userJson) {
    loginPage.doSuccessfulLogin(userJson.username(), userJson.testData().password())
            .checkIsLoaded();
  }

  @TestUser()
  @Test
  @DisplayName("Неуспешный логин. Введён неверный пароль")
  void unsuccessfulLoginWithAnotherPassword(@User(OUTER) UserJson userJson) {
    loginPage.doFailedLogin(userJson.username(), "123")
            .checkError();
  }

  @TestUser()
  @Test
  @DisplayName("Неуспешный логин. Введено неверное имя пользователя")
  void unsuccessfulLoginWithAnotherUserName(@User(OUTER) UserJson userJson) {
    loginPage.doFailedLogin("firefly", userJson.testData().password())
            .checkError();
  }
}
