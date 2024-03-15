package guru.qa.niffler.test.grphql;

import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.gql.GqlUser;
import guru.qa.niffler.model.userdata.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GqlUsersTest extends BaseGraphQLTest {

  @Test
  @ApiLogin(
      user = @TestUser
  )
  void currentUserShouldBeReturned(@User UserJson testUser,
                                   @Token String bearerToken,
                                   @GqlRequestFile("gql/currentUserQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

    final GqlUser response = gatewayGqlApiClient.currentUser(bearerToken, request);
    Assertions.assertEquals(
        testUser.username(),
        response.getData().getUser().getUsername()
    );
  }

}
