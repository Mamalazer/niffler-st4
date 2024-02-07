package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;
import jakarta.persistence.EntityManager;

import java.util.Map;
import java.util.Optional;

import static guru.qa.niffler.db.Database.SPEND;

public class SpendRepositoryHibernate extends JpaService implements SpendRepository {


    public SpendRepositoryHibernate(Database database, EntityManager em) {
        super(database, em);
    }

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        persist(SPEND, spend);
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        persist(SPEND, category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> selectCategory(String categoryName, String userName) {
        return Optional.of(select(
                SPEND,
                "FROM CategoryEntity c WHERE c.category = :category AND c.username = :username",
                Map.of("category", categoryName, "username", userName)
        ));
    }
}
