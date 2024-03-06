package guru.qa.niffler.config;

public class LocalConfig implements Config {

  static final LocalConfig instance = new LocalConfig();

  private LocalConfig() {
  }

  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000";
  }

  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000";
  }

  @Override
  public String jdbcHost() {
    return "localhost";
  }

  @Override
  public String currencyGrpcHost() {
    return "localhost";
  }

  @Override
  public String spendGrpcHost() {
    return "localhost";
  }
}
