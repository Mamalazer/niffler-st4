package guru.qa.niffler.api.register;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.cookie.ThreadSafeCookieManager;
import guru.qa.niffler.api.interceptor.CodeInterceptor;
import io.qameta.allure.Step;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

public class RegisterApiClient extends RestClient {

    private final RegisterApi regApi;

    public RegisterApiClient() {
        super(
                CFG.authUrl(),
                false,
                ScalarsConverterFactory.create(),
                new CodeInterceptor()
        );
        regApi = retrofit.create(RegisterApi.class);
    }

    @Step("Регистрация пользователя '{username}'")
    public void doRegister(String username, String password) throws IOException {
        regApi.startRegister().execute();
        String xsrfToken = ThreadSafeCookieManager.INSTANCE.getCookieValue("XSRF-TOKEN");

        regApi.registerUser(
                "XSRF-TOKEN=" + xsrfToken,
                username,
                password,
                password,
                xsrfToken
        ).execute();
    }
}
