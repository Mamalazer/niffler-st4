package guru.qa.niffler.jupiter.extension.user;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.DbRepositoryConfig;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.annotation.DbUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserCreateExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserCreateExtension.class);

    private final UserRepository userRepository = DbRepositoryConfig.getDbConfig();
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
                .filter(method -> method.isAnnotationPresent(DbUser.class))
                .toList();

        for (Method method : methods) {

            DbUser annotation = method.getAnnotation(DbUser.class);
            String username = annotation.username();
            String password = annotation.password();

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
            userAuth.setAuthorities(Arrays.stream(Authority.values())
                    .map(e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(e);
                        return ae;
                    }).toList()
            );

            userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setCurrency(CurrencyValues.RUB);
            userAuth = userRepository.createInAuth(userAuth);
            userEntity = userRepository.createInUserdata(userEntity);
            userAuthInfo.setUserEntity(userEntity);
            userAuthInfo.setUserAuth(userAuth);
            actualUsers.add(username);
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
