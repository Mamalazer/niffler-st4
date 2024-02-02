package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  UserAuthEntity createInAuth(UserAuthEntity user);

  UserEntity createInUserdata(UserEntity user);

  void deleteInAuthById(UUID id);

  void deleteInUserdataById(UUID id);

  Optional<UserAuthEntity> updateInAuth(UserAuthEntity userInfo);

  Optional<UserEntity> updateInUserData(UserEntity userData);

  Optional<UserAuthEntity> selectUserInfoFromAuthById(UUID id);

  Optional<UserEntity> selectUserInfoFromUserDataById(UUID id);
}
