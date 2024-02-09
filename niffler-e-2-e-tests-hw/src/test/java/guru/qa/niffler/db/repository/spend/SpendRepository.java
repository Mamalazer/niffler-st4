package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;

import java.util.Optional;

public interface SpendRepository {

    SpendEntity createSpend(SpendEntity spend);

    CategoryEntity createCategory(CategoryEntity category);

    Optional<CategoryEntity> findCategory(String categoryName, String userName);
}
