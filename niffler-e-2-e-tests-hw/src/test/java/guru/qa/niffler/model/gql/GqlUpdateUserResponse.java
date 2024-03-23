package guru.qa.niffler.model.gql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlUpdateUserResponse extends GqlResponse<GqlUpdateUserResponse> {

    private GqlUser updateUser;
}
