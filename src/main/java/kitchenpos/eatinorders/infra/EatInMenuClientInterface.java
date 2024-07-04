package kitchenpos.eatinorders.infra;

import java.math.BigDecimal;
import java.util.UUID;

public interface EatInMenuClientInterface {
  void validatorMenuCheckMethod(UUID menuId, BigDecimal price);
}
