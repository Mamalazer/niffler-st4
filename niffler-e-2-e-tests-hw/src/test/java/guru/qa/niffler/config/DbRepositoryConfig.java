package guru.qa.niffler.config;

import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.db.repository.UserRepositorySJdbc;

public class DbRepositoryConfig {

    public static UserRepository getDbConfig() {
        String repository = System.getProperty("repository");

        if ("jdbc".equals(repository)) {
            return new UserRepositoryJdbc();
        } else if ("sjdbc".equals(repository)) {
            return new UserRepositorySJdbc();
        } else {
            throw new IllegalArgumentException("Unknown repository argument");
        }
    }
}
