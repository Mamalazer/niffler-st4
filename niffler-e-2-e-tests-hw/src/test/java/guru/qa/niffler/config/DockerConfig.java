package guru.qa.niffler.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

public class DockerConfig implements Config {

  static final DockerConfig instance = new DockerConfig();

  private DockerConfig() {
  }

  static {
    Configuration.remote = "http://selenoid:4444/wd/hub";
    Configuration.browser = "chrome";
    Configuration.browserVersion = "117.0";
    Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
    Configuration.browserSize = "1980x1024";
  }

  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc";
  }

  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000";
  }

  @Override
  public String gatewayUrl() {
    return "http://gateway.niffler.dc:8090";
  }

  @Override
  public String jdbcHost() {
//    return "niffler-all-db";
    return "localhost";
  }

  @Override
  public String currencyGrpcHost() {
//    return "currency.niffler.dc";
    return "localhost";
  }

  @Override
  public String spendGrpcHost() {
//    return "spend.niffler.dc";
    return "localhost";
  }

  @Override
  public String kafkaAddress() {
    return "kafka:9092";
  }
}
