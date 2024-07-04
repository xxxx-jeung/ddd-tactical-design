package kitchenpos.takeoutorders.infra;

import java.math.BigDecimal;
import java.util.UUID;

public interface TakeoutMenuClientInterface {
  void validatorMenuCheckMethod(UUID menuId, BigDecimal price);
}
