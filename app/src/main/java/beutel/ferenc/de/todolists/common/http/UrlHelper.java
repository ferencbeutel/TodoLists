package beutel.ferenc.de.todolists.common.http;

import java.net.URL;

import lombok.SneakyThrows;

public class UrlHelper {

  public static final String BACKEND_BASE_URL = "http://10.0.2.2:9999";
  public static final String HEALTH_ENDPOINT = "/health";
  public static final String TODOS_ENDPOINT = "/todos";
  public static final String LOGIN_ENDPOINT = "/login";
  public static final String USER_ENDPOINT = "/user";
  public static final String URL_DELIMITER = "/";

  @SneakyThrows
  public static URL toUrl(final String urlString) {
    return new URL(urlString);
  }
}
