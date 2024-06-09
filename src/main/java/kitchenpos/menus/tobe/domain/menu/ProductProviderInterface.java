package kitchenpos.menus.tobe.domain.menu;

import java.util.List;
import kitchenpos.menus.tobe.application.dto.ProductConsumerDto;

public interface ProductProviderInterface {
  MenuProduct findById(ProductConsumerDto productConsumerDto);

  List<MenuProduct> findByIds(List<ProductConsumerDto> productConsumerDtos);
}
