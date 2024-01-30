package guru.qa.niffler.db.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAuthInfo {

    private UserAuthEntity userAuth;
    private UserEntity userEntity;
}
