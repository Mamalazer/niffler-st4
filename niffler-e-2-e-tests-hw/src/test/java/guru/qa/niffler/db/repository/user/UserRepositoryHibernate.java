package guru.qa.niffler.db.repository.user;

import guru.qa.niffler.db.EmfProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.db.models.user.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.db.Database.AUTH;
import static guru.qa.niffler.db.Database.USERDATA;

public class UserRepositoryHibernate extends JpaService implements UserRepository {

  private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public UserRepositoryHibernate() {
    super(
        Map.of(
            AUTH, EmfProvider.INSTANCE.emf(AUTH).createEntityManager(),
            USERDATA, EmfProvider.INSTANCE.emf(USERDATA).createEntityManager()
        )
    );
  }

  @Override
  public UserAuthEntity createInAuth(UserAuthEntity user) {
    String originalPassword = user.getPassword();
    user.setPassword(pe.encode(originalPassword));
    persist(AUTH, user);
//    user.setPassword(originalPassword);
    return user;
  }

  @Override
  public Optional<UserAuthEntity> selectUserInfoFromAuthById(UUID id) {
    return Optional.of(entityManager(AUTH).find(UserAuthEntity.class, id));
  }

  @Override
  public UserEntity createInUserdata(UserEntity user) {
    persist(USERDATA, user);
    return user;
  }

  @Override
  public Optional<UserEntity> selectUserInfoFromUserDataById(UUID id) {
    return Optional.of(entityManager(USERDATA).find(UserEntity.class, id));
  }

  @Override
  public Optional<UserEntity> selectUserInfoFromUserDataByName(String userName) {
    return Optional.of((UserEntity) select(
            USERDATA,
            "FROM UserEntity u WHERE u.username = :username",
            Map.of("username", userName)
    ).get(0));
  }

  @Override
  public void addFriend(String firstUser, String secondUser) {
    UserEntity user1 = selectUserInfoFromUserDataByName(firstUser).get();
    UserEntity user2 = selectUserInfoFromUserDataByName(secondUser).get();

    user1.addFriends(false, user2);
    user2.addFriends(false, user1);

    persist(USERDATA, user1);
    persist(USERDATA, user2);
  }

  @Override
  public void createFriendInvite(String fromUser, String toUser) {
    UserEntity user1 = selectUserInfoFromUserDataByName(fromUser).get();
    UserEntity user2 = selectUserInfoFromUserDataByName(toUser).get();

    user1.addFriends(true, user2);

    persist(USERDATA, user1);
  }

  @Override
  public void deleteInAuthById(UUID id) {
    UserAuthEntity toBeDeleted = selectUserInfoFromAuthById(id).get();
    remove(AUTH, toBeDeleted);
  }

  @Override
  public void deleteInUserdataById(UUID id) {
    UserEntity toBeDeleted = selectUserInfoFromUserDataById(id).get();
    remove(USERDATA, toBeDeleted);
  }

  @Override
  public Optional<UserAuthEntity> updateInAuth(UserAuthEntity userInfo) {
    return Optional.empty();
  }

  @Override
  public Optional<UserEntity> updateInUserData(UserEntity userData) {
    return Optional.empty();
  }
}
