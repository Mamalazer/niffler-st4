package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;
import static guru.qa.niffler.model.UserJson.user;

public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static Map<User.UserType, Queue<UserJson>> users = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationReceivedQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationSendQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> commonQueue = new ConcurrentLinkedQueue<>();

        friendsQueue.add(user("dima", "12345", WITH_FRIENDS));
        invitationReceivedQueue.add(user("duck", "12345", INVITATION_RECIEVED));
        invitationSendQueue.add(user("bee", "12345", INVITATION_SEND));
        commonQueue.add(user("barsik", "12345", COMMON));

        users.put(WITH_FRIENDS, friendsQueue);
        users.put(INVITATION_RECIEVED, invitationReceivedQueue);
        users.put(INVITATION_SEND, invitationSendQueue);
        users.put(COMMON, commonQueue);
    }

    @Override
    public void beforeEach(ExtensionContext context) {

        Map<User.UserType, UserJson> testCandidates = new HashMap<>();

        List<Parameter> params = Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .map(Executable::getParameters)
                .flatMap(Arrays::stream)
                .filter(param -> param.isAnnotationPresent(User.class))
                .filter(param -> param.getType().isAssignableFrom(UserJson.class))
                .toList();

        for (Parameter parameter : params) {
            User.UserType annotationType = parameter.getAnnotation(User.class).value();

            if (testCandidates.containsKey(annotationType)) {
                continue;
            }

            UserJson testCandidate = null;
            Queue<UserJson> queue = users.get(annotationType);
            while (testCandidate == null) {
                testCandidate = queue.poll();
            }
            testCandidates.put(annotationType, testCandidate);
        }

        context.getStore(NAMESPACE).put(context.getUniqueId(), testCandidates);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Map<?, ?> usersFromTest = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), Map.class);

        for (Map.Entry<?, ?> entry : usersFromTest.entrySet()) {
            users.get(entry.getKey()).add((UserJson) entry.getValue());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class) &&
                parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (UserJson) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class)
                .get(parameterContext.getParameter().getAnnotation(User.class).value());
    }
}
