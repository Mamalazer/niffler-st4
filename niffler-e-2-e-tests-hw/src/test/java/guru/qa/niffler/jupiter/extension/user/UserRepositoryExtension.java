package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.config.DbRepositoryConfig;
import guru.qa.niffler.db.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class UserRepositoryExtension implements TestInstancePostProcessor {
  @Override
  public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
    for (Field field : o.getClass().getDeclaredFields()) {
      if (field.getType().isAssignableFrom(UserRepository.class)) {
        field.setAccessible(true);
        field.set(o, DbRepositoryConfig.getDbConfig());
      }
    }
  }
}
