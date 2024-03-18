package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.api.register.RegisterApiClient;
import guru.qa.niffler.api.spend.SpendApiClient;
import guru.qa.niffler.api.userdata.UserDataApiClient;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.model.category.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.data.DataUtils;
import retrofit2.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.codeborne.selenide.Selenide.sleep;

public class RestCreteUserExtension extends CreateUserExtension {

    private static final RegisterApiClient REGISTER_API_CLIENT = new RegisterApiClient();
    private static final UserDataApiClient USER_DATA_API_CLIENT = new UserDataApiClient();
    private static final CategoryApiClient CATEGORY_API_CLIENT = new CategoryApiClient();
    private static final SpendApiClient SPEND_API_CLIENT = new SpendApiClient();

    @Override
    public UserJson createUser(TestUser user) throws IOException, ParseException {
        String username = user.username().isEmpty()
                ? DataUtils.generateRandomUsername()
                : user.username();
        String password = user.password().isEmpty()
                ? "12345"
                : user.password();
        UserJson createdUser = null;

        REGISTER_API_CLIENT.doRegister(username, password);

        for (int i = 0; i < 10; i++) {
            sleep(500L);
            Response<UserJson> userInfo = USER_DATA_API_CLIENT.getUserInfo(username);
            if (userInfo.isSuccessful()) {
                createdUser = userInfo.body();
                break;
            }
        }

        if (!user.category().fake()) {
            createdUser = createCategory(user, createdUser);
        }

        if (!user.spend().fake()) {
            createdUser = createSpend(user, createdUser);
        }

        return new UserJson(
                createdUser.id(),
                createdUser.username(),
                createdUser.firstname(),
                createdUser.surname(),
                guru.qa.niffler.model.currency.CurrencyValues.valueOf(createdUser.currency().name()),
                createdUser.photo() == null ? "" : createdUser.photo(),
                createdUser.friendState(),
                new TestData(
                        password,
                        null,
                        createdUser.testData() == null ? null : createdUser.testData().category(),
                        createdUser.testData() == null ? null : createdUser.testData().spend()
                )
        );
    }

    @Override
    public UserJson createCategory(TestUser user, UserJson createdUser) throws IOException {
        CategoryJson categoryJson = new CategoryJson(null, user.category().category(), createdUser.username());
        CATEGORY_API_CLIENT.createCategory(categoryJson);

        return new UserJson(
                createdUser.id(),
                createdUser.username(),
                createdUser.firstname(),
                createdUser.surname(),
                guru.qa.niffler.model.currency.CurrencyValues.valueOf(createdUser.currency().name()),
                createdUser.photo() == null ? "" : createdUser.photo(),
                createdUser.friendState(),
                new TestData(
                        createdUser.testData() == null ? "" : createdUser.testData().password(),
                        null,
                        categoryJson,
                        createdUser.testData() == null ? null : createdUser.testData().spend()
                )
        );
    }

    @Override
    public UserJson createSpend(TestUser user, UserJson createdUser) throws IOException, ParseException {

        SpendJson spendJson = new SpendJson(
                null,
                new SimpleDateFormat("yyyy-MM-dd").parse(user.spend().spendDate()),
                user.spend().category(),
                user.spend().currency(),
                user.spend().amount(),
                user.spend().description(),
                user.spend().username()
        );

        SPEND_API_CLIENT.createSpend(spendJson);

        return new UserJson(
                createdUser.id(),
                createdUser.username(),
                createdUser.firstname(),
                createdUser.surname(),
                guru.qa.niffler.model.currency.CurrencyValues.valueOf(createdUser.currency().name()),
                createdUser.photo() == null ? "" : createdUser.photo(),
                createdUser.friendState(),
                new TestData(
                        createdUser.testData() == null ? "" : createdUser.testData().password(),
                        null,
                        createdUser.testData() == null ? null : createdUser.testData().category(),
                        spendJson
                )
        );
    }
}
