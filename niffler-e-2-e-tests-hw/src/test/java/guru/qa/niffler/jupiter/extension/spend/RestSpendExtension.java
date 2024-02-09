package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.api.CategoryApi;
import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class RestSpendExtension extends SpendExtension {

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();
  private static final Retrofit RETROFIT = new Retrofit.Builder()
          .client(HTTP_CLIENT)
          .baseUrl("http://127.0.0.1:8093")
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

  private static final CategoryApi CATEGORY_API = RETROFIT.create(CategoryApi.class);
  private static final SpendApi SPEND_API = RETROFIT.create(SpendApi.class);

  @Override
  SpendJson create(SpendJson spend) {
    CategoryJson categoryJson = new CategoryJson(
            null,
            spend.category(),
            spend.username()
    );

    try {
      CATEGORY_API.addCategory(categoryJson).execute();
      return SPEND_API.addSpend(spend).execute().body();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
