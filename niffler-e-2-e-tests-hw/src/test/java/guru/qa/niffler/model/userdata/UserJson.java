package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.jupiter.annotation.UserQueue;
import guru.qa.niffler.model.category.CategoryJson;
import guru.qa.niffler.model.currency.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.userdata.wsdl.FriendState;

import java.util.UUID;

public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("photo")
    String photo,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("friendState")
    FriendState friendState,
    @JsonIgnore
    TestData testData) {

    public static UserJson user(String username, String password, UserQueue.UserType userType, CategoryJson category, SpendJson spend) {
        return new UserJson(
                null,
                username,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                new TestData(
                        password,
                        userType,
                        category,
                        spend
                )
        );
    }
}
