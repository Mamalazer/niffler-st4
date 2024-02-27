package guru.qa.niffler.jupiter.extension.login;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SessionStorage;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.auth.AuthApiClient;
import guru.qa.niffler.api.cookie.ThreadSafeCookieManager;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.db.models.user.UserAuthInfo;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.extension.context.ContextHolderExtension;
import guru.qa.niffler.jupiter.extension.user.UserCreateExtension;
import guru.qa.niffler.utils.oauth.OauthUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

  private static final Config CFG = Config.getInstance();
  private final AuthApiClient authApiClient = new AuthApiClient();

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);


  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    ApiLogin apiLogin = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        ApiLogin.class
    ).orElse(null);

    if (apiLogin != null) {
      final String codeVerifier = OauthUtils.generateCodeVerifier();
      final String codeChallenge = OauthUtils.generateCodeChallange(codeVerifier);
      setCodeVerifier(extensionContext, codeVerifier);
      setCodChallenge(extensionContext, codeChallenge);

      if (apiLogin.user().isRunnable()) {
        ExtensionContext userExtensionContext = ContextHolderExtension.Holder.INSTANCE.context();
        UserAuthEntity userEnity = userExtensionContext.getStore(UserCreateExtension.NAMESPACE)
                .get(userExtensionContext.getUniqueId(), UserAuthInfo.class)
                .getUserAuth();
        authApiClient.doLogin(extensionContext, userEnity.getUsername(), userEnity.getPassword());
      } else {
        authApiClient.doLogin(extensionContext, apiLogin.username(), apiLogin.password());
      }

      Selenide.open(CFG.frontUrl());
      SessionStorage sessionStorage = Selenide.sessionStorage();
      sessionStorage.setItem(
          "codeChallenge", getCodChallenge(extensionContext)
      );
      sessionStorage.setItem(
          "id_token", getToken(extensionContext)
      );
      sessionStorage.setItem(
          "codeVerifier", getCodeVerifier(extensionContext)
      );

      WebDriverRunner.getWebDriver().manage().addCookie(
          jsessionCookie()
      );
      Selenide.refresh();
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) {
    ThreadSafeCookieManager.INSTANCE.removeAll();
  }

  public static void setCodeVerifier(ExtensionContext context, String codeVerifier) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("code_verifier", codeVerifier);
  }

  public static void setCodChallenge(ExtensionContext context, String codeChallenge) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("code_challenge", codeChallenge);
  }

  public static void setCode(ExtensionContext context, String code) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("code", code);
  }

  public static void setToken(ExtensionContext context, String token) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("token", token);
  }

  public static String getCodeVerifier(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("code_verifier", String.class);
  }

  public static String getCodChallenge(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("code_challenge", String.class);
  }

  public static String getCode(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("code", String.class);
  }

  public static String getToken(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("token", String.class);
  }

  public static String getCsrfToken() {
    return ThreadSafeCookieManager.INSTANCE.getCookieValue("XSRF-TOKEN");
  }

  public Cookie jsessionCookie() {
    return new Cookie(
        "JSESSIONID",
        ThreadSafeCookieManager.INSTANCE.getCookieValue("JSESSIONID")
    );
  }
}
