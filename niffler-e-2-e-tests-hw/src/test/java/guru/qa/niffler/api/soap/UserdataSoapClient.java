package guru.qa.niffler.api.soap;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.converter.jaxb.JaxbConverterFactory;
import guru.qa.niffler.userdata.wsdl.*;
import io.qameta.allure.Step;

public class UserdataSoapClient extends RestClient {

  private final SoapUserdataApi userdataApi;

  public UserdataSoapClient() {
    super(
        CFG.userDataServiceUrl(),
        false,
        new JaxbConverterFactory("niffler-userdata")
    );
    userdataApi = retrofit.create(SoapUserdataApi.class);
  }

  @Step
  public CurrentUserResponse currentUser(CurrentUserRequest request) throws Exception {
    return userdataApi.currentUser(request).execute()
        .body();
  }

  @Step
  public AllUsersResponse allUsers(AllUsersRequest request) throws Exception {
    return userdataApi.allUsers(request).execute()
            .body();
  }

  @Step
  public UpdateUserInfoResponse updateUserInfo(UpdateUserInfoRequest request) throws Exception {
    return userdataApi.updateUserInfo(request).execute()
            .body();
  }

  @Step
  public FriendsResponse getFriends(FriendsRequest request) throws Exception {
    return userdataApi.getFriends(request).execute()
            .body();
  }

  @Step
  public InvitationsResponse getInvitations(InvitationsRequest request) throws Exception {
    return userdataApi.getInvitations(request).execute()
            .body();
  }

  @Step
  public AcceptInvitationResponse acceptInvitation(AcceptInvitationRequest request) throws Exception {
    return userdataApi.acceptInvitation(request).execute()
            .body();
  }

  @Step
  public DeclineInvitationResponse declineInvitation(DeclineInvitationRequest request) throws Exception {
    return userdataApi.declineInvitation(request).execute()
            .body();
  }

  @Step
  public AddFriendResponse addFriend(AddFriendRequest request) throws Exception {
    return userdataApi.addFriend(request).execute()
            .body();
  }

  @Step
  public RemoveFriendResponse removeFriend(RemoveFriendRequest request) throws Exception {
    return userdataApi.removeFriend(request).execute()
            .body();
  }
}
