package guru.qa.niffler.api.register;

import retrofit2.Call;
import retrofit2.http.*;

public interface RegisterApi {

    @GET("/register")
    Call<String> startRegister();

    @POST("/register")
    @FormUrlEncoded
    Call<String> registerUser(
            @Header("Cookie") String cookie,
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf
    );
}
