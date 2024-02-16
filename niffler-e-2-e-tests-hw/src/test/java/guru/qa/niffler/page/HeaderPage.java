package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.condition.PhotoCondition.photoFromClasspath;

public class HeaderPage extends BasePage<HeaderPage> {

    private final SelenideElement avatar = $(".header__avatar");

    @Step("Перейти на страницу со списком всех пользователей")
    public AllPeoplePage goToAllPeoplePage() {
        $x("//a[contains(@href, 'people')]").click();
        return new AllPeoplePage();
    }

    @Step("Перейти на страницу со списком друзей")
    public FriendsPage goToFriendsPage() {
        $x("//a[contains(@href, 'friends')]").click();
        return new FriendsPage();
    }

    @Step("Перейти на страницу профиля")
    public ProfilePage goToProfilePage() {
        $x("//a[contains(@href, 'profile')]").click();
        return new ProfilePage();
    }

    @Step("check avatar")
    public HeaderPage checkAvatar(String imageName) {
        avatar.shouldHave(photoFromClasspath(imageName));
        return this;
    }
}
