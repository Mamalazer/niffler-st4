package guru.qa.niffler.page;

import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage extends BasePage<MainPage> {

    private final SpendingTable spendingTable = new SpendingTable();
    public HeaderPage header = new HeaderPage();

    @Step("Удалить выбранные траты")
    public MainPage deleteSelectedSpendings() {
        $(byText("Delete selected"))
                .click();
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

    @Step("Показать сегодняшние траты")
    public MainPage showTodaySpends() {
        $x("//button[text() = 'Today']").click();
        return this;
    }

    @Step("Показать траты за последнюю неделю")
    public MainPage showLastWeekSpends() {
        $x("//button[text() = 'Last week']").click();
        return this;
    }

    @Step("Показать траты за последний месяц")
    public MainPage showLastMonthSpends() {
        $x("//button[text() = 'Last month']").click();
        return this;
    }

    @Step("Показать траты за всё время")
    public MainPage showAllTimeSpends() {
        $x("//button[text() = 'All time']").click();
        return this;
    }
}
