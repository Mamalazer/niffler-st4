package guru.qa.niffler.page.component;


import guru.qa.niffler.model.spend.SpendJson;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendCollectionCondition.spends;

public class SpendingTable extends BaseComponent<SpendingTable>{

  public SpendingTable() {
    super($(".spendings-table tbody"));
  }

  public SpendingTable checkSpends(SpendJson... expectedSpends) {
    getSelf().$$("tr").should(spends(expectedSpends));
    return this;
  }

  @Step("Выбрать трату по индексу '{index}'")
  public SpendingTable selectSpendingByIndex(int index) {
    getSelf().$$("tr")
            .get(index-1)
            .$$("td")
            .first()
            .scrollIntoView(true)
            .click();
    return this;
  }

  @Step("Выбрать трату по тексту '{text}'")
  public SpendingTable selectSpendingByText(String text) {
    getSelf().$$("tr")
            .find(text(text))
            .$$("td")
            .first()
            .scrollIntoView(true)
            .click();
    return this;
  }

  @Step("Убедиться, что траты отсутствуют")
  public SpendingTable checkThatSpendingsEmpty() {
            getSelf()
            .$$("tr")
            .shouldHave(size(0));
    return this;
  }
}
