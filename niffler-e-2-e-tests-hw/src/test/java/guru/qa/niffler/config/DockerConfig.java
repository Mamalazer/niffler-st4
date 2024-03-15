package guru.qa.niffler.config;

public class DockerConfig implements Config {

  static final DockerConfig instance = new DockerConfig();

  private DockerConfig() {
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
}
