package guru.qa.niffler.model.register;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistrationModel(

    @JsonProperty("_csrf")
    String csrf,
    String username,
    String password,
    String passwordSubmit) {

}
