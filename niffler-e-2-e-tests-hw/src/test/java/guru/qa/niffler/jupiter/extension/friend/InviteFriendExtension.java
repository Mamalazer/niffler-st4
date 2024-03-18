package guru.qa.niffler.jupiter.extension.friend;

import guru.qa.niffler.jupiter.annotation.InviteFriend;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

public abstract class InviteFriendExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace INVITE_FRIEND_NAMESPACE
            = ExtensionContext.Namespace.create(InviteFriendExtension.class);
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<InviteFriend> annotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                InviteFriend.class
        );

        if (annotation.isPresent()) {
            InviteFriend friends = annotation.get();
            inviteFriend(friends.fromUser(), friends.toUser());
        }
    }

    abstract void inviteFriend(String fromUser, String toUser);
}
