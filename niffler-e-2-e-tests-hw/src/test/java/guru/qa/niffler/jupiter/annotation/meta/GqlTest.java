package guru.qa.niffler.jupiter.annotation.meta;

import guru.qa.niffler.jupiter.extension.context.ContextHolderExtension;
import guru.qa.niffler.jupiter.extension.friend.DataBaseAddFriendExtension;
import guru.qa.niffler.jupiter.extension.friend.DataBaseInviteFriendExtension;
import guru.qa.niffler.jupiter.extension.user.DataBaseCreteUserExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({ContextHolderExtension.class,
        AllureJunit5.class,
        DataBaseCreteUserExtension.class,
        DataBaseAddFriendExtension.class,
        DataBaseInviteFriendExtension.class})
public @interface GqlTest {
}