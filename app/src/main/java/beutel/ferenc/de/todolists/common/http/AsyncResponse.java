package beutel.ferenc.de.todolists.common.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsyncResponse {

  private final int responseCode;
  private final String response;
}