package guru.qa.niffler.db.models.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAuthInfo {

    private UserAuthEntity userAuth;
    private UserEntity userEntity;
}
