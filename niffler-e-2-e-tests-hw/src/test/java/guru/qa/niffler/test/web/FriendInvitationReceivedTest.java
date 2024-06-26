package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserQueue;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.UserQueue.UserType.INVITATION_RECIEVED;
import static guru.qa.niffler.jupiter.annotation.UserQueue.UserType.INVITATION_SEND;

public class FriendInvitationReceivedTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();

    @BeforeEach
    void doLogin() {
        Selenide.open(Config.getInstance().frontUrl());
        welcomePage.goToLoginPage();
    }

    @Test
    @DisplayName("У пользователя отображается приглашение в друзья на странице со списком всех пользователей")
    void userHasInviteInAllPeoplePage(@UserQueue(INVITATION_RECIEVED) UserJson user1, @UserQueue(INVITATION_SEND) UserJson user2) {
        loginPage.doSuccessfulLogin(user1.username(), user1.testData().password())
                .header.goToAllPeoplePage()
                .checkThatInvitationReceived(user2.username());
    }

    @Test
    @DisplayName("У пользователя отображается приглашение в друзья на странице с друзьями")
    void userHasInviteInFriendsPage(@UserQueue(INVITATION_RECIEVED) UserJson user1, @UserQueue(INVITATION_SEND) UserJson user2) {
        loginPage.doSuccessfulLogin(user1.username(), user1.testData().password())
                .header.goToFriendsPage()
                .checkThatInvitationReceived(user2.username());
    }
}
