package guru.qa.niffler.jupiter.extension.friend;

import guru.qa.niffler.jupiter.annotation.Friends;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

public abstract class AddFriendExtension  implements BeforeEachCallback {

    public static final ExtensionContext.Namespace ADD_FRIEND_NAMESPACE
            = ExtensionContext.Namespace.create(AddFriendExtension.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<Friends> annotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                Friends.class
        );

        if (annotation.isPresent()) {
            Friends friends = annotation.get();
            addFriends(friends.firstUser(), friends.secondUser());
        }
    }

    abstract void addFriends(String firstUser, String secondUser);
}
