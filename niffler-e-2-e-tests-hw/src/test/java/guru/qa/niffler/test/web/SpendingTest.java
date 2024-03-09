package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.currency.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.User.Point.OUTER;

public class SpendingTest extends BaseWebTest {

  protected WelcomePage welcomePage = new WelcomePage();

  @BeforeEach
  void doLogin() {
    Selenide.open(Config.getInstance().frontUrl());
  }

  @TestUser(username = "bober", password = "12345")
  @GenerateSpend(
      username = "bober",
      description = "QA.GURU Advanced 4",
      amount = 72500.00,
      category = "Обучение",
      currency = CurrencyValues.RUB,
      spendDate = "2024-02-07"
  )
  @Test
  @DisplayName("Проверка удаления траты")
  void spendingShouldBeDeletedByButtonDeleteSpending(@User(OUTER) UserJson userJson, SpendJson spend) {
    welcomePage.goToLoginPage()
            .doSuccessfulLogin(userJson.username(), userJson.testData().password())
            .getSpendingTable()
            .selectSpendingByText(spend.description())
            .returnFocusToPage(MainPage.class)
            .deleteSelectedSpendings()
            .getSpendingTable()
            .checkThatSpendingsEmpty();
  }

  @TestUser(username = "firefly", password = "12345")
  @GenerateSpend(
          username = "firefly",
          description = "Лечение зубов",
          amount = 10000.00,
          category = "Лечение",
          currency = CurrencyValues.RUB,
          spendDate = "2024-02-15"
  )
  @Test
  @DisplayName("Проверка трат")
  void checkSpendingTableInfo(@User(OUTER) UserJson userJson, SpendJson spend) {
    welcomePage.goToLoginPage()
            .doSuccessfulLogin(userJson.username(), userJson.testData().password())
            .getSpendingTable()
            .checkSpends(spend);
  }
}
