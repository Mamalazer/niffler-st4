package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

    private final SelenideElement spendsTable = $(".spendings-table tbody");
    private final SpendingTable spendingTable = new SpendingTable();
    public HeaderPage header = new HeaderPage();

    @Step("Выбрать трату по описанию '{description}'")
    public MainPage selectSpendingByDescription(String description) {
        spendsTable
                .$$("tr")
                .find(text(description))
                .$("td")
                .scrollTo()
                .click();
        return this;
    }

    @Step("Удалить выбранные траты")
    public MainPage deleteSelectedSpendings() {
        $(byText("Delete selected"))
                .click();
        return this;
    }

    @Step("Убедиться, что траты отсутствуют")
    public MainPage checkThatSpendingsEmpty() {
        spendsTable
                .$$("tr")
                .shouldHave(size(0));
        return this;
    }

    @Step("Убедиться, что Main page загрузилась")
    public MainPage checkIsLoaded() {
        $(".main-content").shouldBe(visible);
        return this;
    }

    public SpendingTable getSpendingTable() {
        return spendingTable;
    }
}
