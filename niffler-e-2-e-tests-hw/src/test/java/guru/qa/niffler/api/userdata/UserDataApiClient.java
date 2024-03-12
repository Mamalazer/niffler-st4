package guru.qa.niffler.api.userdata;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.userdata.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import java.io.IOException;

public class UserDataApiClient extends RestClient {

    private final UserDataApi userDataApi;

    public UserDataApiClient() {
        super(Config.getInstance().userDataServiceUrl());
        this.userDataApi = retrofit.create(UserDataApi.class);
    }

    @Step("Получить информацию о пользователе '{userName}'")
    public Response<UserJson> getUserInfo(String userName) throws IOException {
        return userDataApi.getUserInfo(userName)
                .execute();
    }
}
