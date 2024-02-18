package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private final SelenideElement usernameField = $("input[name='username']");
    private final SelenideElement passwordField = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement formError = $(".form__error");

    @Step("Авторизоваться под пользователем '{login}'")
    public MainPage doSuccessfulLogin(String login, String password) {
        setLogin(login);
        setPassword(password);
        submitForm();
        return new MainPage();
    }

    @Step("Авторизоваться под пользователем '{login}'")
    public LoginPage doFailedLogin(String login, String password) {
        setLogin(login);
        setPassword(password);
        submitForm();
        return this;
    }

    @Step("Указать логин {login}")
    public LoginPage setLogin(String login) {
        usernameField.setValue(login);
        return this;
    }

    @Step("Указать пароль {password}")
    public LoginPage setPassword(String password) {
        passwordField.setValue(password);
        return this;
    }

    @Step("Подтвердить отправку пользовательских данных")
    public LoginPage submitForm() {
        submitButton.click();
        return this;
    }

    @Step("Убедиться, что отображается ошибка ввода пользовательских данных")
    public LoginPage checkError() {
        formError.shouldBe(visible);
        return this;
    }
}
