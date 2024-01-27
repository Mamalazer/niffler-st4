package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserRepository {

  UserAuthEntity createInAuth(UserAuthEntity user);

  UserEntity createInUserdata(UserEntity user);

  void deleteInAuthById(UUID id);

  void deleteInUserdataById(UUID id);

  void updateInAuthById(UserAuthEntity userInfo);

  void updateInUserDataById(UserEntity userData);

  UserAuthEntity selectUserInfoFromAuthById(UUID id);

  UserEntity selectUserInfoFromUserDataById(UUID id);
}
