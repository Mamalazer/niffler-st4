package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.userdata.wsdl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static guru.qa.niffler.jupiter.annotation.User.Point.OUTER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserdataSoapTest extends BaseSoapTest {

  @TestUser
  @Test
  void currentUserInfoShouldContainsUsername(@User(OUTER) UserJson user) throws Exception {
    CurrentUserRequest currentUserRequest = new CurrentUserRequest();
    currentUserRequest.setUsername(user.username());

    final CurrentUserResponse response = userdataSoapClient.currentUser(currentUserRequest);
    Assertions.assertEquals(
        user.username(),
        response.getUser().getUsername()
    );
  }

  @TestUsers({@TestUser, @TestUser, @TestUser})
  @Test
  void checkThatAllUsersArePresent(@User(OUTER) UserJson[] users) throws Exception {
    AllUsersRequest allUsersRequest = new AllUsersRequest();
    allUsersRequest.setUsername(users[0].username());

    final AllUsersResponse response = userdataSoapClient.allUsers(allUsersRequest);
    assertAll(
            () -> assertEquals(
                    2,
                    response.getUser().size()
            ),
            () -> assertEquals(
                    Arrays.stream(users)
                            .map(UserJson::username)
                            .filter(name -> !name.equals(users[0].username()))
                            .toList(),
                    response.getUser().stream()
                            .map(x -> x.getUsername())
                            .toList()
            )
    );
  }

  @TestUser()
  @Test
  void checkThatUserIsUpdated(@User(OUTER) UserJson user) throws Exception {
    UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
    guru.qa.niffler.userdata.wsdl.User updatedUser = new guru.qa.niffler.userdata.wsdl.User();
    updatedUser.setCurrency(Currency.EUR);
    updatedUser.setFirstname("mouse");
    updatedUser.setSurname("Игоревич");
    updatedUser.setUsername(user.username());
    updateUserInfoRequest.setUser(updatedUser);

    final UpdateUserInfoResponse response = userdataSoapClient.updateUserInfo(updateUserInfoRequest);
    assertAll(
            () -> assertEquals(
                    updatedUser.getUsername(),
                    response.getUser().getUsername()
            ),
            () -> assertEquals(
                    updatedUser.getFirstname(),
                    response.getUser().getFirstname()
            ),
            () -> assertEquals(
                    updatedUser.getSurname(),
                    response.getUser().getSurname()
            ),
            () -> assertEquals(
                    updatedUser.getCurrency(),
                    response.getUser().getCurrency()
            )
    );
  }

  @TestUsers({
          @TestUser(username = "parrot", password = "12345"),
          @TestUser(username = "crocodile", password = "12345")
  })
  @Friends(firstUser = "parrot", secondUser = "crocodile")
  @Test
  void checkFriend() throws Exception {
    FriendsRequest friendsRequest = new FriendsRequest();
    friendsRequest.setUsername("parrot");
    friendsRequest.setIncludePending(false);

    final FriendsResponse response = userdataSoapClient.getFriends(friendsRequest);
    assertEquals(
            "crocodile",
            response.getUser().get(0).getUsername()
    );
  }

  @TestUsers({
          @TestUser(username = "lamb", password = "12345"),
          @TestUser(username = "wolf", password = "12345")
  })
  @InviteFriend(fromUser = "wolf", toUser = "lamb")
  @Test
  void checkInvitations() throws Exception {
    InvitationsRequest invitationsRequest = new InvitationsRequest();
    invitationsRequest.setUsername("lamb");

    final InvitationsResponse response = userdataSoapClient.getInvitations(invitationsRequest);
    assertEquals(
            "wolf",
            response.getUser().get(0).getUsername()
    );
  }

  @TestUsers({
          @TestUser(username = "bunny", password = "12345"),
          @TestUser(username = "moose", password = "12345")
  })
  @InviteFriend(fromUser = "bunny", toUser = "moose")
  @Test
  void checkInvitationAccept() throws Exception {
    AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
    Friend friend = new Friend();
    friend.setUsername("bunny");
    acceptInvitationRequest.setUsername("moose");
    acceptInvitationRequest.setInvitation(friend);

    FriendsRequest friendsRequest = new FriendsRequest();
    friendsRequest.setUsername("bunny");
    friendsRequest.setIncludePending(false);

    userdataSoapClient.acceptInvitation(acceptInvitationRequest);
    final FriendsResponse response = userdataSoapClient.getFriends(friendsRequest);

    assertEquals(
            "moose",
            response.getUser().get(0).getUsername()
    );
  }

  @TestUsers({
          @TestUser(username = "zebra", password = "12345"),
          @TestUser(username = "groundhog", password = "12345")
  })
  @InviteFriend(fromUser = "zebra", toUser = "groundhog")
  @Test
  void checkInvitationDecline() throws Exception {
    DeclineInvitationRequest declineInvitationRequest = new DeclineInvitationRequest();
    Friend friend = new Friend();
    friend.setUsername("zebra");
    declineInvitationRequest.setUsername("groundhog");
    declineInvitationRequest.setInvitation(friend);

    FriendsRequest friendsRequest = new FriendsRequest();
    friendsRequest.setUsername("zebra");
    friendsRequest.setIncludePending(false);

    userdataSoapClient.declineInvitation(declineInvitationRequest);
    final FriendsResponse response = userdataSoapClient.getFriends(friendsRequest);

    assertEquals(
            Collections.emptyList(),
            response.getUser()
    );
  }

  @TestUsers({
          @TestUser(username = "skunk", password = "12345"),
          @TestUser(username = "snail", password = "12345")
  })
  @Test
  void checkSendInvitation() throws Exception {
    AddFriendRequest addFriendRequest = new AddFriendRequest();
    Friend friend = new Friend();
    friend.setUsername("skunk");
    addFriendRequest.setUsername("snail");
    addFriendRequest.setFriend(friend);

    InvitationsRequest invitationsRequest = new InvitationsRequest();
    invitationsRequest.setUsername("skunk");

    userdataSoapClient.addFriend(addFriendRequest);
    final InvitationsResponse response = userdataSoapClient.getInvitations(invitationsRequest);

    assertEquals(
            "snail",
            response.getUser().get(0).getUsername()
    );
  }

  @TestUsers({
          @TestUser(username = "squirrel", password = "12345"),
          @TestUser(username = "porcupine", password = "12345")
  })
  @Friends(firstUser = "squirrel", secondUser = "porcupine")
  @Test
  void checkRemoveFriend() throws Exception {
    RemoveFriendRequest removeFriendRequest = new RemoveFriendRequest();
    removeFriendRequest.setUsername("squirrel");
    removeFriendRequest.setFriendUsername("porcupine");

    FriendsRequest friendsRequest = new FriendsRequest();
    friendsRequest.setUsername("squirrel");
    friendsRequest.setIncludePending(false);

    userdataSoapClient.removeFriend(removeFriendRequest);
    final FriendsResponse response = userdataSoapClient.getFriends(friendsRequest);

    assertEquals(
            Collections.emptyList(),
            response.getUser()
    );
  }
}
