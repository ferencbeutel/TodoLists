package beutel.ferenc.de.todolists.login.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Data;

import beutel.ferenc.de.todolists.login.domain.User.UserBuilder;

@Data
@Builder
@JsonDeserialize(builder = UserBuilder.class)
public class User {

  private final String username;
  private final String password;

  @JsonPOJOBuilder(withPrefix = "")
  public static final class UserBuilder {

  }
}
