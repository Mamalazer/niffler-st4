package guru.qa.niffler.api.soap;

import guru.qa.niffler.userdata.wsdl.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SoapUserdataApi {

  @POST("/ws")
  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  Call<CurrentUserResponse> currentUser(@Body CurrentUserRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<AllUsersResponse> allUsers(@Body AllUsersRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<UpdateUserInfoResponse> updateUserInfo(@Body UpdateUserInfoRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<FriendsResponse> getFriends(@Body FriendsRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<InvitationsResponse> getInvitations(@Body InvitationsRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<AcceptInvitationResponse> acceptInvitation(@Body AcceptInvitationRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<DeclineInvitationResponse> declineInvitation(@Body DeclineInvitationRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<AddFriendResponse> addFriend(@Body AddFriendRequest request);

  @POST("/ws")
  @Headers({
          "Content-Type: text/xml",
          "Accept-Charset: utf-8"
  })
  Call<RemoveFriendResponse> removeFriend(@Body RemoveFriendRequest request);
}
