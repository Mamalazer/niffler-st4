package guru.qa.niffler.config;

import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.EmfProvider;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.db.repository.spend.SpendRepositoryHibernate;
import guru.qa.niffler.db.repository.spend.SpendRepositoryJdbc;

import static guru.qa.niffler.db.Database.SPEND;

public class DbSpendRepositoryConfig {

    public static SpendRepository getDbConfig() {
        String repository = System.getProperty("repository");

        if ("jdbc".equals(repository)) {
            return new SpendRepositoryJdbc();
        } else if ("sjdbc".equals(repository)) {
            return new SpendRepositoryJdbc();
        } else if ("hibernate".equals(repository)) {
            return new SpendRepositoryHibernate(
                    Database.SPEND,
                    EmfProvider.INSTANCE.emf(SPEND).createEntityManager()
            );
        } else {
            throw new IllegalArgumentException("Unknown repository argument");
        }
    }
}
