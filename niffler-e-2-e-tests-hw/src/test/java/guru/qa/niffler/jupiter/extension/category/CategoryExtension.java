package guru.qa.niffler.jupiter.extension.category;

import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.category.CategoryJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(CategoryExtension.class);

    private static final CategoryApiClient CATEGORY_API = new CategoryApiClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<Category> category = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                Category.class
        );

        if (category.isPresent()) {
            Category categoryData = category.get();
            CategoryJson categoryJson = new CategoryJson(
                    null,
                    categoryData.category(),
                    categoryData.username()
            );

            CategoryJson created = CATEGORY_API.createCategory(categoryJson);
            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), created);
        }
    }
}
