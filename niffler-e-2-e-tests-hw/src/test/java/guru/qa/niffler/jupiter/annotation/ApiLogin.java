package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.context.ContextHolderExtension;
import guru.qa.niffler.jupiter.extension.login.ApiLoginExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({ContextHolderExtension.class, ApiLoginExtension.class})
public @interface ApiLogin {
  String username();

  String password();
}
