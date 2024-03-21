package guru.qa.niffler.jupiter.extension.friend;

import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryHibernate;

public class DataBaseInviteFriendExtension extends InviteFriendExtension {

    UserRepository userRepository = new UserRepositoryHibernate();

    @Override
    void inviteFriend(String fromUser, String toUser) {
        userRepository.createFriendInvite(fromUser, toUser);
    }
}
