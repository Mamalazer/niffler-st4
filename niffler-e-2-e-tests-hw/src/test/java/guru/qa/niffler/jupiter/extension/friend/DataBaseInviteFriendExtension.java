package guru.qa.niffler.jupiter.extension.friend;

import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.db.repository.user.UserRepositoryJdbc;

public class DataBaseInviteFriendExtension extends InviteFriendExtension {

    UserRepository userRepository = new UserRepositoryJdbc();

    @Override
    void inviteFriend(String fromUser, String toUser) {
        userRepository.createFriendInvite(fromUser, toUser);
    }
}
