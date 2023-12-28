package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameField = $("input[name='username']");
    private final SelenideElement passwordField = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");

    @Step("Авторизоваться под пользователем '{login}'")
    public MainPage doLogin(String login, String password) {
        usernameField.setValue(login);
        passwordField.setValue(password);
        submitButton.click();
        return new MainPage();
    }
}
