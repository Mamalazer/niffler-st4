package guru.qa.niffler.jupiter.extension.friend;

import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryJdbc;

public class DataBaseAddFriendExtension extends AddFriendExtension {

    UserRepository userRepository = new UserRepositoryJdbc();

    @Override
    void addFriends(String firstUser, String secondUser) {
        userRepository.addFriend(firstUser, secondUser);
    }
}
