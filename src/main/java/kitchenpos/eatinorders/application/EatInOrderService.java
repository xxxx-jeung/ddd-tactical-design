package kitchenpos.eatinorders.application;

import java.math.BigDecimal;
import java.util.*;
import kitchenpos.eatinorders.domain.*;
import kitchenpos.eatinorders.domain.EatInOrder;
import kitchenpos.eatinorders.infra.EatInMenuClientInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EatInOrderService {
  private final EatInOrderRepository orderRepository;
  private final EatInOrderTableRepository orderTableRepository;
  private final EatInMenuClientInterface eatInMenuClientInterface;

  public EatInOrderService(
          final EatInOrderRepository orderRepository,
          final EatInOrderTableRepository orderTableRepository,
          final EatInMenuClientInterface eatInMenuClientInterface) {
    this.orderRepository = orderRepository;
    this.orderTableRepository = orderTableRepository;
    this.eatInMenuClientInterface = eatInMenuClientInterface;
  }

  @Transactional
  public EatInOrderResponseDto create(final EatInOrderRequestDto request) {

    final List<EatInOrderLineItemRequestDto> eatInOrderLineItemRequestDtos =
        request.getEatInOrderLineItemRequestDtos();

    final List<EatInOrderLineItem> eatInOrderLineItems =
        this.createEatInOrderLineItems(eatInOrderLineItemRequestDtos);

    final EatInOrderTable orderTable = findOrderTable(request.getOrderTableId());

    final EatInOrder eatInOrder = EatInOrder.createOrder(eatInOrderLineItems, orderTable);
    orderRepository.save(eatInOrder);
    return EatInOrderResponseDto.create(eatInOrder);
  }

  @Transactional
  public EatInOrderResponseDto accept(final UUID orderId) {

    final EatInOrder eatInOrder = this.getEatInOrder(orderId);
    eatInOrder.accepted();
    return EatInOrderResponseDto.create(eatInOrder);
  }

  @Transactional
  public EatInOrderResponseDto serve(final UUID orderId) {
    final EatInOrder eatInOrder = this.getEatInOrder(orderId);
    eatInOrder.serve();
    return EatInOrderResponseDto.create(eatInOrder);
  }

  @Transactional
  public EatInOrderResponseDto complete(final UUID orderId) {
    final EatInOrder eatInOrder = this.getEatInOrder(orderId);
    eatInOrder.completed();
    return EatInOrderResponseDto.create(eatInOrder);
  }

  @Transactional(readOnly = true)
  public List<EatInOrderResponseDto> findAll() {
    return orderRepository.findAll().stream().map(EatInOrderResponseDto::create).toList();
  }

  private List<EatInOrderLineItem> createEatInOrderLineItems(
      final List<EatInOrderLineItemRequestDto> eatInOrderLineItemRequestDtos) {

    if (Objects.isNull(eatInOrderLineItemRequestDtos)) {
      throw new IllegalArgumentException();
    }

    final List<EatInOrderLineItem> eatInOrderLineItems = new ArrayList<>();
    for (EatInOrderLineItemRequestDto eatInOrderLineItemRequestDto :
        eatInOrderLineItemRequestDtos) {
      final UUID menuId = eatInOrderLineItemRequestDto.getMenuId();
      final BigDecimal price = eatInOrderLineItemRequestDto.getPrice();
      final long quantity = eatInOrderLineItemRequestDto.getQuantity();

      eatInMenuClientInterface.validatorMenuCheckMethod(menuId, price);
      eatInOrderLineItems.add(EatInOrderLineItem.createItem(quantity, menuId));
    }
    return eatInOrderLineItems;
  }

  private EatInOrderTable findOrderTable(UUID orderTableId) {
    final EatInOrderTable orderTable =
        orderTableRepository.findById(orderTableId).orElseThrow(NoSuchElementException::new);
    if (!orderTable.getOccupied()) {
      throw new IllegalStateException();
    }

    return orderTable;
  }

  private EatInOrder getEatInOrder(final UUID orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new NoSuchElementException("주문이 존재하지 않습니다."));
  }
}
