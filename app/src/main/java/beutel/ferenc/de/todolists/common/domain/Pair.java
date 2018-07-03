package beutel.ferenc.de.todolists.common.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pair<R, T> {

  private final R left;
  private final T right;
}
