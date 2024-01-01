package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.user.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;


@ExtendWith(UsersQueueExtension.class)
public class FriendsTest2 {

  @BeforeEach
//  void doLogin(@User(WITH_FRIENDS) UserJson user) {
  void doLogin() {
//    Selenide.open("http://127.0.0.1:3000/main");
//    $("a[href*='redirect']").click();
//    $("input[name='username']").setValue(user.username());
//    $("input[name='password']").setValue(user.testData().password());
//    $("button[type='submit']").click();
  }

  @Test
  void friendsTableShouldNotBeEmpty0(@User(WITH_FRIENDS) UserJson user, @User(INVITATION_SEND) UserJson user2) throws Exception {
    UserJson user_1 = user;
    UserJson user_2 = user2;
    Thread.sleep(3000);
  }

  @Test
  void friendsTableShouldNotBeEmpty1(@User(INVITATION_RECIEVED) UserJson user) throws Exception {
    Thread.sleep(3000);
  }

  @Test
  void friendsTableShouldNotBeEmpty2(@User(WITH_FRIENDS) UserJson user, @User(INVITATION_SEND) UserJson user2) throws Exception {
    Thread.sleep(3000);
  }
}
