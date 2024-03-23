package guru.qa.niffler.db.repository.user;

import guru.qa.niffler.db.models.user.UserAuthEntity;
import guru.qa.niffler.db.models.user.UserEntity;

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

  Optional<UserEntity> selectUserInfoFromUserDataByName(String userName);

  void addFriend(String firstUser, String secondUser);

  void createFriendInvite(String fromUser, String toUser);
}
