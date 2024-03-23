package guru.qa.niffler.api.gql;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.model.gql.GqlRequest;
import guru.qa.niffler.model.gql.GqlUpdateUserResponse;
import guru.qa.niffler.model.gql.GqlUserResponse;
import guru.qa.niffler.model.gql.GqlUsersResponse;

public class GatewayGqlApiClient extends RestClient {

  private final GraphQlGatewayApi graphQlGatewayApi;

  public GatewayGqlApiClient() {
    super(
        CFG.gatewayUrl()
    );
    graphQlGatewayApi = retrofit.create(GraphQlGatewayApi.class);
  }

  public GqlUserResponse currentUser(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.currentUser(bearerToken, request).execute()
        .body();
  }

  public GqlUserResponse getFriends(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.getFriends(bearerToken, request).execute()
            .body();
  }

  public GqlUpdateUserResponse updateUser(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.updateUser(bearerToken, request).execute()
            .body();
  }

  public GqlUsersResponse users(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.users(bearerToken, request).execute()
            .body();
  }
}
