package guru.qa.niffler.model.gql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlUserResponse extends GqlResponse<GqlUserResponse> {

    private GqlUser user;
}
