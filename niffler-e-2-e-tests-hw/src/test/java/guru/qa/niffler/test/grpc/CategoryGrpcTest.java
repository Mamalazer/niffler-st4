package guru.qa.niffler.test.grpc;

import guru.qa.grpc.niffler.grpc.Categories;
import guru.qa.grpc.niffler.grpc.Category;
import guru.qa.grpc.niffler.grpc.CategoryRequest;
import guru.qa.grpc.niffler.grpc.UserName;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.annotation.GenerateCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryGrpcTest extends BaseCategoryGrpcTest {

    @DbUser()
    @DisplayName("Создание категории")
    @Test
    void createCategory(UserAuthEntity userAuth) {
        CategoryRequest categoryRequest = CategoryRequest.newBuilder()
                .setCategory("Развлечения")
                .setUsername(userAuth.getUsername())
                .build();

        Category category = blockingStub.addCategory(categoryRequest);

        Assertions.assertEquals("Развлечения", category.getCategory());
        Assertions.assertEquals(userAuth.getUsername(), category.getUsername());
    }

    @DbUser(username = "Mouse", password = "12345", isRunnable = true)
    @GenerateCategory(category = "Обучение", username = "Mouse")
    @DisplayName("Получение всех категорий пользователя")
    @Test
    void getAllCategories(UserAuthEntity userAuth) {
        UserName user = UserName.newBuilder()
                .setName(userAuth.getUsername())
                .build();

        Categories categories = blockingStub.getCategories(user);

        Assertions.assertEquals(1, categories.getCategoriesList().size());
        Assertions.assertEquals(userAuth.getUsername(), categories.getCategoriesList().get(0).getUsername());
        Assertions.assertEquals("Обучение", categories.getCategoriesList().get(0).getCategory());
    }
}
