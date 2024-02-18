package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.config.DbSpendRepositoryConfig;
import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.model.spend.SpendJson;

public class DatabaseSpendExtension extends SpendExtension {

    private final SpendRepository userRepository = DbSpendRepositoryConfig.getDbConfig();

    @Override
    SpendJson create(SpendJson spend) {

        SpendEntity spendEntity = new SpendEntity();
        CategoryEntity category = new CategoryEntity();

        category.setCategory(spend.category());
        category.setUsername(spend.username());

        category = userRepository.createCategory(category);

        spendEntity.setUsername(spend.username());
        spendEntity.setCurrency(spend.currency());
        spendEntity.setSpendDate(spend.spendDate());
        spendEntity.setAmount(spend.amount());
        spendEntity.setDescription(spend.description());
        spendEntity.setCategory(category);

        spendEntity = userRepository.createSpend(spendEntity);

        return new SpendJson(
                spendEntity.getId(),
                spendEntity.getSpendDate(),
                spendEntity.getCategory().getCategory(),
                spendEntity.getCurrency(),
                spendEntity.getAmount(),
                spendEntity.getDescription(),
                spendEntity.getUsername()
        );
    }
}
