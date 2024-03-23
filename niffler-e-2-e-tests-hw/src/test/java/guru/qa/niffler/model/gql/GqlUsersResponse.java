package guru.qa.niffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GqlUsersResponse extends GqlResponse<GqlUsersResponse> {

    private List<GqlUser> users;
}
