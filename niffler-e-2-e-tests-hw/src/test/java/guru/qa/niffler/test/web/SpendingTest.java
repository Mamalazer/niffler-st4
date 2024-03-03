package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.jupiter.extension.user.UserCreateExtension;
import guru.qa.niffler.model.currency.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserCreateExtension.class)
public class SpendingTest extends BaseWebTest {

  protected WelcomePage welcomePage = new WelcomePage();

  @BeforeEach
  void doLogin() {
    Selenide.open(Config.getInstance().frontUrl());
  }

  @DbUser(username = "bober", password = "12345")
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
  void spendingShouldBeDeletedByButtonDeleteSpending(UserAuthEntity userAuth, SpendJson spend) {
    welcomePage.goToLoginPage()
            .doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword())
            .getSpendingTable()
            .selectSpendingByText(spend.description())
            .returnFocusToPage(MainPage.class)
            .deleteSelectedSpendings()
            .getSpendingTable()
            .checkThatSpendingsEmpty();
  }

  @DbUser(username = "firefly", password = "12345")
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
  void checkSpendingTableInfo(UserAuthEntity userAuth, SpendJson spend) {
    welcomePage.goToLoginPage()
            .doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword())
            .getSpendingTable()
            .checkSpends(spend);
  }
}
