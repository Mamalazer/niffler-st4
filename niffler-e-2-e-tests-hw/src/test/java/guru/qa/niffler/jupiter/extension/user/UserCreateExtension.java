package guru.qa.niffler.jupiter.extension.user;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.DbUserRepositoryConfig;
import guru.qa.niffler.db.models.user.*;
import guru.qa.niffler.db.repository.user.UserRepository;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.utils.allure.JsonAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UserCreateExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserCreateExtension.class);

    private final UserRepository userRepository = DbUserRepositoryConfig.getDbConfig();
    private final JsonAppender jsonAppender = new JsonAppender();
    private final Faker faker = new Faker();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {

        UserAuthEntity userAuth;
        UserEntity userEntity;
        List<String> actualUsers = new ArrayList<>();
        List<Method> actualMethods = new ArrayList<>();
        UserAuthInfo userAuthInfo = new UserAuthInfo();

        actualMethods.add(extensionContext.getRequiredTestMethod());
        Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(actualMethods::add);

        List<Method> methods = actualMethods.stream()
                .filter(method -> method.isAnnotationPresent(TestUser.class) || method.isAnnotationPresent(ApiLogin.class))
                .toList();

        for (Method method : methods) {

            TestUser testUserAnno = null;
            ApiLogin apiLoginAnno = null;
            String username;
            String password;

            if (method.isAnnotationPresent(ApiLogin.class)) {
                if (method.getAnnotation(ApiLogin.class).user().isRunnable()) {
                    testUserAnno = method.getAnnotation(ApiLogin.class).user();
                } else {
                    apiLoginAnno = method.getAnnotation(ApiLogin.class);
                }
            } else if (method.isAnnotationPresent(TestUser.class)) {
                testUserAnno = method.getAnnotation(TestUser.class);
            }

            if (testUserAnno != null) {
                username = testUserAnno.username();
                password = testUserAnno.password();
            } else {
                username = apiLoginAnno.username();
                password = apiLoginAnno.password();
            }

            if (actualUsers.contains(username)) {
                continue;
            }

            if (username.isEmpty() && password.isEmpty()) {
                username = faker.name().firstName();
                password = faker.random().hex(12);
            }

            userAuth = new UserAuthEntity();
            userAuth.setUsername(username);
            userAuth.setPassword(password);
            userAuth.setEnabled(true);
            userAuth.setAccountNonExpired(true);
            userAuth.setAccountNonLocked(true);
            userAuth.setCredentialsNonExpired(true);
            AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
                    a -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(a);
                        return ae;
                    }
            ).toArray(AuthorityEntity[]::new);

            userAuth.addAuthorities(authorities);

            userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setCurrency(CurrencyValues.RUB);
            userAuth = userRepository.createInAuth(userAuth);
            userEntity = userRepository.createInUserdata(userEntity);
            userAuthInfo.setUserEntity(userEntity);
            userAuthInfo.setUserAuth(userAuth);
            actualUsers.add(username);

            jsonAppender.attachJson("User info", userEntity);
        }

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), userAuthInfo);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        UserAuthInfo userAuthInfo = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), UserAuthInfo.class);

        userRepository.deleteInAuthById(userAuthInfo.getUserAuth().getId());
        userRepository.deleteInUserdataById(userAuthInfo.getUserEntity().getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserAuthEntity.class);
    }

    @Override
    public UserAuthEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), UserAuthInfo.class)
                .getUserAuth();
    }
}
