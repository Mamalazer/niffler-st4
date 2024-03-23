package guru.qa.niffler.api.gql;

import guru.qa.niffler.model.gql.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GraphQlGatewayApi {

  @POST("/graphql")
  Call<GqlUserResponse> currentUser(@Header("Authorization") String bearerToken,
                            @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUserResponse> getFriends(@Header("Authorization") String bearerToken,
                                   @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUsersResponse> users(@Header("Authorization") String bearerToken,
                               @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUpdateUserResponse> updateUser(@Header("Authorization") String bearerToken,
                                         @Body GqlRequest gqlRequest);
}
