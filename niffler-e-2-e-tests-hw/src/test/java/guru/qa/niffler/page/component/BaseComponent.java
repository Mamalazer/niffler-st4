package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import lombok.SneakyThrows;

public abstract class BaseComponent<T extends BaseComponent> {

  protected final SelenideElement self;

  public BaseComponent(SelenideElement self) {
    this.self = self;
  }

  public SelenideElement getSelf() {
    return self;
  }

  @SneakyThrows
  public <T> T returnFocusToPage(Class<T> page) {
      return (T) page.newInstance();
  }
}
