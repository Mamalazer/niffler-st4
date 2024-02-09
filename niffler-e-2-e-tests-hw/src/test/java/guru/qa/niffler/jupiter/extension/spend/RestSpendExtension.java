package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.api.CategoryApi;
import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class RestSpendExtension extends SpendExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(RestSpendExtension.class);

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();
  private static final Retrofit RETROFIT = new Retrofit.Builder()
          .client(HTTP_CLIENT)
          .baseUrl("http://127.0.0.1:8093")
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

  private static final CategoryApi CATEGORY_API = RETROFIT.create(CategoryApi.class);
  private static final SpendApi SPEND_API = RETROFIT.create(SpendApi.class);

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    Optional<GenerateSpend> spend = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        GenerateSpend.class
    );

    if (spend.isPresent()) {
      GenerateSpend spendData = spend.get();
      SpendJson spendJson = new SpendJson(
          null,
          new SimpleDateFormat("yyyy-MM-dd").parse(spendData.spendDate()),
          spendData.category(),
          spendData.currency(),
          spendData.amount(),
          spendData.description(),
          spendData.username()
      );

      SpendJson created = create(spendJson);
      extensionContext.getStore(NAMESPACE)
          .put(extensionContext.getUniqueId(), created);
    }
  }

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

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter()
            .getType()
            .isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(RestSpendExtension.NAMESPACE)
            .get(extensionContext.getUniqueId(), SpendJson.class);
  }
}
