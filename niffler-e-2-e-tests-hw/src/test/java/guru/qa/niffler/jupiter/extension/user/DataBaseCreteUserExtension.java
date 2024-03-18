package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.EmfProvider;
import guru.qa.niffler.db.models.spend.CategoryEntity;
import guru.qa.niffler.db.models.spend.SpendEntity;
import guru.qa.niffler.db.models.user.*;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.db.repository.spend.SpendRepositoryHibernate;
import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.data.DataUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static guru.qa.niffler.db.Database.SPEND;

public class DataBaseCreteUserExtension extends CreateUserExtension {

    private static final UserRepository USER_REPOSITORY = new UserRepositoryHibernate();
    private static final SpendRepository SPEND_REPOSITORY = new SpendRepositoryHibernate(
            Database.SPEND, EmfProvider.INSTANCE.emf(SPEND).createEntityManager()
    );

    @Override
    public UserJson createUser(TestUser user) throws ParseException {
        String username = user.username().isEmpty()
                ? DataUtils.generateRandomUsername()
                : user.username();
        String password = user.password().isEmpty()
                ? "12345"
                : user.password();

        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUsername(username);
        userAuth.setPassword(password);
        userAuth.setEnabled(true);
        userAuth.setAccountNonExpired(true);
        userAuth.setAccountNonLocked(true);
        userAuth.setCredentialsNonExpired(true);
        AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
                a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        userAuth.addAuthorities(authorities);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);

        USER_REPOSITORY.createInAuth(userAuth);
        USER_REPOSITORY.createInUserdata(userEntity);

        UserJson createdUser = new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getSurname(),
                guru.qa.niffler.model.currency.CurrencyValues.valueOf(userEntity.getCurrency().name()),
                userEntity.getPhoto() == null ? "" : new String(userEntity.getPhoto()),
                null,
                new TestData(
                        password,
                        null,
                        null,
                        null
                )
        );

        if (!user.category().fake()) {
            createdUser = createCategory(user, createdUser);
        }

        if (!user.spend().fake()) {
            createdUser = createSpend(user, createdUser);
        }

        return createdUser;
    }

    @Override
    public UserJson createCategory(TestUser user, UserJson createdUser) {
        CategoryEntity category = new CategoryEntity();
        category.setCategory(user.category().category());
        category.setUsername(user.category().username());
        category = SPEND_REPOSITORY.createCategory(category);

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
                        category.toJson(),
                        createdUser.testData() == null ? null : createdUser.testData().spend()
                )
        );
    }

    @Override
    public UserJson createSpend(TestUser user, UserJson createdUser) throws ParseException {
        SpendEntity spendEntity = new SpendEntity();
        spendEntity.setUsername(user.spend().username());
        spendEntity.setCurrency(user.spend().currency());
        spendEntity.setSpendDate(new SimpleDateFormat("yyyy-MM-dd").parse(user.spend().spendDate()));
        spendEntity.setAmount(user.spend().amount());
        spendEntity.setDescription(user.spend().description());
        spendEntity.setCategory(CategoryEntity.toEntity(createdUser.testData().category()));
        spendEntity = SPEND_REPOSITORY.createSpend(spendEntity);

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
                        spendEntity.toJson()
                )
        );
    }
}
