package guru.qa.niffler.test;

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
  protected MainPage mainPage = new MainPage();

  @DbUser(username = "bober", password = "12345")
  @BeforeEach
  void doLogin(UserAuthEntity userAuth) {
    Selenide.open(Config.getInstance().frontUrl());
    welcomePage.goToLoginPage()
            .doSuccessfulLogin(userAuth.getUsername(), userAuth.getPassword());
  }

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
  void spendingShouldBeDeletedByButtonDeleteSpending(SpendJson spend) {

    mainPage.selectSpendingByDescription(spend.description())
            .deleteSelectedSpendings()
            .checkThatSpendingsEmpty();
  }
}
