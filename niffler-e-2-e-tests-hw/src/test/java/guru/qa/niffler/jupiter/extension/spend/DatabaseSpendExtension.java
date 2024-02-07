package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.config.DbSpendRepositoryConfig;
import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.UUID;

public class DatabaseSpendExtension extends SpendExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(DatabaseSpendExtension.class);

    private final SpendRepository userRepository = DbSpendRepositoryConfig.getDbConfig();

    @Override
    SpendJson create(SpendJson spend) {

        SpendEntity spendEntity = new SpendEntity();
        CategoryEntity category = new CategoryEntity();

        category.setId(UUID.fromString("7c42541f-230d-4fa0-bf37-d1209affcfcc"));

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

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<GenerateSpend> spend = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                GenerateSpend.class
        );

        if (spend.isPresent()) {
            GenerateSpend spendData = spend.get();
            SpendJson spendJson = new SpendJson(
                    null,
                    new SimpleDateFormat("yyyy-MM-dd").parse(spendData.spendDate()),
                    spendData.category(),
                    spendData.currency(),
                    spendData.amount(),
                    spendData.description(),
                    spendData.username()
            );

            SpendJson created = create(spendJson);
            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), created);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DatabaseSpendExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
