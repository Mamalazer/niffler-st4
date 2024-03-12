package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.db.models.user.*;
import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.data.DataUtils;

import java.util.Arrays;

public class DataBaseCreteUserExtension extends CreateUserExtension {

  private static UserRepository userRepository = new UserRepositoryHibernate();

  @Override
  public UserJson createUser(TestUser user) {
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

    userRepository.createInAuth(userAuth);
    userRepository.createInUserdata(userEntity);
    return new UserJson(
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
  }

  @Override
  public UserJson createCategory(TestUser user, UserJson createdUser) {
    return null;
  }

  @Override
  public UserJson createSpend(TestUser user, UserJson createdUser) {
    return null;
  }
}
