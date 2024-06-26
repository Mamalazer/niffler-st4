package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.jupiter.annotation.UserQueue;
import guru.qa.niffler.model.userdata.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.UserQueue.UserType.*;
import static guru.qa.niffler.model.userdata.UserJson.user;

public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static final Map<UserQueue.UserType, Queue<UserJson>> USERS = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationReceivedQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationSendQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> commonQueue = new ConcurrentLinkedQueue<>();

        friendsQueue.add(user("dima", "12345", WITH_FRIENDS, null, null));
        friendsQueue.add(user("elephant", "12345", WITH_FRIENDS, null, null));
        invitationReceivedQueue.add(user("duck", "12345", INVITATION_RECIEVED, null, null));
        invitationSendQueue.add(user("bee", "12345", INVITATION_SEND, null, null));
        commonQueue.add(user("barsik", "12345", COMMON, null, null));

        USERS.put(WITH_FRIENDS, friendsQueue);
        USERS.put(INVITATION_RECIEVED, invitationReceivedQueue);
        USERS.put(INVITATION_SEND, invitationSendQueue);
        USERS.put(COMMON, commonQueue);
    }

    @Override
    public void beforeEach(ExtensionContext context) {

        Map<UserQueue.UserType, UserJson> testCandidates = new HashMap<>();
        List<Method> actualMethods = new ArrayList<>();

        actualMethods.add(context.getRequiredTestMethod());
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(actualMethods::add);

        List<Parameter> params = actualMethods.stream()
                .map(Executable::getParameters)
                .flatMap(Arrays::stream)
                .filter(param -> param.isAnnotationPresent(UserQueue.class))
                .filter(param -> param.getType().isAssignableFrom(UserJson.class))
                .toList();

        for (Parameter parameter : params) {
            UserQueue.UserType annotationType = parameter.getAnnotation(UserQueue.class).value();

            if (testCandidates.containsKey(annotationType)) {
                continue;
            }

            UserJson testCandidate = null;
            Queue<UserJson> queue = USERS.get(annotationType);
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
            USERS.get(entry.getKey()).add((UserJson) entry.getValue());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class) &&
                parameterContext.getParameter().isAnnotationPresent(UserQueue.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (UserJson) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class)
                .get(parameterContext.getParameter().getAnnotation(UserQueue.class).value());
    }
}
