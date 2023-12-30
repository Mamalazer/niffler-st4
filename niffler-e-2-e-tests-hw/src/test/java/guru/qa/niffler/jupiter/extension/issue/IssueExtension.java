package guru.qa.niffler.jupiter.extension.issue;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class IssueExtension implements ExecutionCondition {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(IssueExtension.class);

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();
  private static final Retrofit RETROFIT = new Retrofit.Builder()
      .client(HTTP_CLIENT)
      .baseUrl("https://api.github.com")
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final GhApi ghApi = RETROFIT.create(GhApi.class);

  @SneakyThrows
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    DisabledByIssue disabledByIssue = AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        DisabledByIssue.class
    ).orElse(
        AnnotationSupport.findAnnotation(
            context.getRequiredTestClass(),
            DisabledByIssue.class
        ).orElse(null)
    );

    if (disabledByIssue != null) {
      JsonNode responseBody = ghApi.issue(
          "Bearer " + System.getenv("GH_TOKEN"),
          disabledByIssue.value()
      ).execute().body();

      if (responseBody != null) {
        return "open".equals(responseBody.get("state").asText())
                ? ConditionEvaluationResult.disabled("Disabled by issue " + disabledByIssue.value())
                : ConditionEvaluationResult.enabled("Issue " + disabledByIssue.value() + "is closed");
      }
    }
    return ConditionEvaluationResult.enabled("Annotation not found");
  }
}
