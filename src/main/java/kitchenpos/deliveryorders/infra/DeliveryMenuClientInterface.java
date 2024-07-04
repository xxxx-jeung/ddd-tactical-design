package kitchenpos.deliveryorders.infra;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryMenuClientInterface {
  void validatorMenuCheckMethod(UUID menuId, BigDecimal price);
}
