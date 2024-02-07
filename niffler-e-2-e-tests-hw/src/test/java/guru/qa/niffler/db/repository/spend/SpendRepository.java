package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;

public interface SpendRepository {

    SpendEntity createSpend(SpendEntity spend);

    CategoryEntity createCategory(CategoryEntity category);
}
