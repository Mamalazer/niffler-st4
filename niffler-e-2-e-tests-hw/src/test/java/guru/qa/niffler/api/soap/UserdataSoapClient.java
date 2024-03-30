package guru.qa.niffler.api.soap;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.converter.jaxb.JaxbConverterFactory;
import guru.qa.niffler.userdata.wsdl.CurrentUserRequest;
import guru.qa.niffler.userdata.wsdl.CurrentUserResponse;

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

  public CurrentUserResponse currentUser(CurrentUserRequest request) throws Exception {
    return userdataApi.currentUser(request).execute()
        .body();
  }
}
