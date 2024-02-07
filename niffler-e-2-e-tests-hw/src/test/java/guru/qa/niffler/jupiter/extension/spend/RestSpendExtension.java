package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class RestSpendExtension extends SpendExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(RestSpendExtension.class);

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    Optional<GenerateSpend> spend = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        GenerateSpend.class
    );

    if (spend.isPresent()) {
      GenerateSpend spendData = spend.get();
      Date spendDate;

      try {
        spendDate = new SimpleDateFormat("yyyy-MM-dd").parse(spendData.spendDate());
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }

      SpendJson spendJson = new SpendJson(
          null,
          spendDate,
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

    OkHttpClient httpClient = new OkHttpClient.Builder().build();
    Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    SpendApi spendApi = retrofit.create(SpendApi.class);

    try {
      return spendApi.addSpend(spend).execute().body();
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
