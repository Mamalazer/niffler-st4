package guru.qa.niffler.jupiter.extension.friend;

import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryHibernate;

public class DataBaseAddFriendExtension extends AddFriendExtension {

    UserRepository userRepository = new UserRepositoryHibernate();

    @Override
    void addFriends(String firstUser, String secondUser) {
        userRepository.addFriend(firstUser, secondUser);
    }
}
