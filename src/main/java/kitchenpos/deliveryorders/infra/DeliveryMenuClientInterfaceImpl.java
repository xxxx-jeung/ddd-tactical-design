package kitchenpos.deliveryorders.infra;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.menus.domain.menu.Menu;
import kitchenpos.menus.domain.menu.MenuRepository;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMenuClientInterfaceImpl implements DeliveryMenuClientInterface {
  private final MenuRepository menuRepository;

  public DeliveryMenuClientInterfaceImpl(MenuRepository menuRepository) {
    this.menuRepository = menuRepository;
  }

  @Override
  public void validatorMenuCheckMethod(final UUID menuId, final BigDecimal price) {
    final Menu menu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);

    if (!menu.isDisplayed()) {
      throw new IllegalStateException();
    }

    if (menu.getPrice().compareTo(price) != 0) {
      throw new IllegalArgumentException();
    }
  }
}
