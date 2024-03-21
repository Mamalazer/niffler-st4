package guru.qa.niffler.test.grphql;

import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.currency.CurrencyValues;
import guru.qa.niffler.model.gql.GqlUpdateUserResponse;
import guru.qa.niffler.model.gql.GqlUser;
import guru.qa.niffler.model.gql.GqlUserResponse;
import guru.qa.niffler.model.gql.GqlUsersResponse;
import guru.qa.niffler.model.userdata.UserJson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.jupiter.annotation.User.Point.OUTER;
import static org.junit.jupiter.api.Assertions.*;

public class GqlUsersTest extends BaseGraphQLTest {

  @Test
  @ApiLogin(user = @TestUser)
  void currentUserShouldBeReturned(@User UserJson testUser,
                                   @Token String bearerToken,
                                   @GqlRequestFile("gql/currentUserQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

    final GqlUserResponse response = gatewayGqlApiClient.currentUser(bearerToken, request);
    assertEquals(
        testUser.username(),
        response.getData().getUser().getUsername()
    );
  }

  @ApiLogin(user = @TestUser(username = "panda", password = "12345"))
  @TestUser(username = "turtle", password = "12345", friends = @Friends(firstUser = "turtle", secondUser = "panda"))
  @Test
  void userShouldHaveOneFriend(@GqlRequestFile("gql/getFriendsQuery.json") guru.qa.niffler.model.gql.GqlRequest request,
                               @User(OUTER) UserJson testUser,
                               @Token String bearerToken) throws Exception {

    final GqlUserResponse response = gatewayGqlApiClient.getFriends(bearerToken, request);

    assertAll(
            () -> assertEquals(
                    1,
                    response.getData().getUser().getFriends().size()
            ),
            () -> assertEquals(
                    testUser.username(),
                    response.getData().getUser().getFriends().get(0).getUsername()
            )
    );
  }

  @ApiLogin(user = @TestUser(username = "cat", password = "12345"))
  @TestUser(username = "firefly", password = "12345", inviteFriend = @InviteFriend(fromUser = "firefly", toUser = "cat"))
  @Test
  void userShouldHaveOneInvite(@GqlRequestFile("gql/getFriendsQuery.json") guru.qa.niffler.model.gql.GqlRequest request,
                               @User(OUTER) UserJson testUser,
                               @Token String bearerToken) throws Exception {

    final GqlUserResponse response = gatewayGqlApiClient.getFriends(bearerToken, request);

    assertAll(
            () -> assertEquals(
                    1,
                    response.getData().getUser().getInvitations().size()
            ),
            () -> assertEquals(
                    testUser.username(),
                    response.getData().getUser().getInvitations().get(0).getUsername()
            )
    );
  }

  @Test
  @ApiLogin(user = @TestUser)
  void userShouldBeUpdated(@Token String bearerToken,
                           @GqlRequestFile("gql/updateUserQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

    final GqlUpdateUserResponse response = gatewayGqlApiClient.updateUser(bearerToken, request);

    assertAll(
            () -> assertEquals(
                    "Pizzly",
                    response.getData().getUpdateUser().getFirstname()
            ),
            () -> assertEquals(
                    "Pizzlyvich",
                    response.getData().getUpdateUser().getSurname()
            ),
            () -> assertEquals(
                    CurrencyValues.EUR,
                    response.getData().getUpdateUser().getCurrency()
            )
    );
  }

  @Test
  @ApiLogin(user = @TestUser)
  @TestUsers({@TestUser, @TestUser, @TestUser})
  void userShouldHaveListOfAnotherUsers(@User(OUTER) UserJson[] users,
                                        @Token String bearerToken,
                                        @GqlRequestFile("gql/usersQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

    final GqlUsersResponse response = gatewayGqlApiClient.users(bearerToken, request);
    List<String> currentNames = response.getData().getUsers().stream()
            .map(GqlUser::getUsername)
            .toList();
    List<String> expectedNames = Arrays.stream(users)
            .map(UserJson::username)
            .toList();

    assertTrue(currentNames.containsAll(expectedNames));
  }

  @ApiLogin(user = @TestUser(username = "jaguar", password = "12345"))
  @TestUser(username = "bull", password = "12345", friends = @Friends(firstUser = "jaguar", secondUser = "bull"))
  @Test
  void twoFriendsSubQueryErrorCheck(@GqlRequestFile("gql/getFriends2FriedsSubQuery.json") guru.qa.niffler.model.gql.GqlRequest request,
                                    @Token String bearerToken) throws Exception {

    final GqlUserResponse response = gatewayGqlApiClient.getFriends(bearerToken, request);

    assertAll(
            () -> assertEquals(
                    1,
                    response.getErrors().size()
            ),
            () -> assertEquals(
                    "Can`t fetch over 2 friends sub-queries",
                    response.getErrors().get(0).message()
            ),
            () -> assertEquals(
                    "BAD_REQUEST",
                    response.getErrors().get(0).extensions().get("classification")
            )
    );
  }

  @ApiLogin(user = @TestUser(username = "cow", password = "12345"))
  @TestUser(username = "owl", password = "12345", inviteFriend = @InviteFriend(fromUser = "owl", toUser = "cow"))
  @Test
  void twoInvitationsSubQueryErrorCheck(@GqlRequestFile("gql/getFriends2InvitationsSubQuery.json") guru.qa.niffler.model.gql.GqlRequest request,
                                        @Token String bearerToken) throws Exception {

    final GqlUserResponse response = gatewayGqlApiClient.getFriends(bearerToken, request);

    assertAll(
            () -> assertEquals(
                    1,
                    response.getErrors().size()
            ),
            () -> assertEquals(
                    "Can`t fetch over 2 invitations sub-queries",
                    response.getErrors().get(0).message()
            ),
            () -> assertEquals(
                    "BAD_REQUEST",
                    response.getErrors().get(0).extensions().get("classification")
            )
    );
  }
}
