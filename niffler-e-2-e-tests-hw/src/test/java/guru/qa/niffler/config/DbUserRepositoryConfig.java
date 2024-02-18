package guru.qa.niffler.config;

import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryHibernate;
import guru.qa.niffler.db.repository.user.UserRepositoryJdbc;
import guru.qa.niffler.db.repository.user.UserRepositorySJdbc;

public class DbUserRepositoryConfig {

    public static UserRepository getDbConfig() {
        String repository = System.getProperty("repository");

        if ("jdbc".equals(repository)) {
            return new UserRepositoryJdbc();
        } else if ("sjdbc".equals(repository)) {
            return new UserRepositorySJdbc();
        } else if ("hibernate".equals(repository)) {
            return new UserRepositoryHibernate();
        } else {
            throw new IllegalArgumentException("Unknown repository argument");
        }
    }
}
