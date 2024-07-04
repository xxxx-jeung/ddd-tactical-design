package kitchenpos.deliveryorders.application;

import java.math.BigDecimal;
import java.util.*;
import kitchenpos.deliveryorders.domain.*;
import kitchenpos.deliveryorders.infra.DeliveryMenuClientInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryOrderService {
  private final DeliveryOrderRepository orderRepository;
  private final KitchenridersClient kitchenridersClient;
  private final DeliveryMenuClientInterface deliveryMenuClientInterface;

  public DeliveryOrderService(
      final DeliveryOrderRepository orderRepository,
      final KitchenridersClient kitchenridersClient,
      final DeliveryMenuClientInterface deliveryMenuClientInterface) {
    this.orderRepository = orderRepository;
    this.kitchenridersClient = kitchenridersClient;
    this.deliveryMenuClientInterface = deliveryMenuClientInterface;
  }

  @Transactional
  public DeliveryOrderResponseDto create(final DeliveryOrderRequestDto request) {

    final List<DeliveryOrderLineItemRequestDto> deliveryOrderLineItemRequestDtos =
        request.getDeliveryOrderLineItemRequestDtos();

    final List<DeliveryOrderLineItem> deliveryOrderLineItems =
        this.createDeliveryOrderLineItems(deliveryOrderLineItemRequestDtos);
    final DeliveryOrder deliveryOrder =
        DeliveryOrder.createOrder(deliveryOrderLineItems, request.getDeliveryAddress());

    return DeliveryOrderResponseDto.create(orderRepository.save(deliveryOrder));
  }

  @Transactional
  public DeliveryOrderResponseDto accept(final UUID orderId) {
    final DeliveryOrder order = this.getDeliveryOrder(orderId);

    order.accept(kitchenridersClient);
    return DeliveryOrderResponseDto.create(order);
  }

  @Transactional
  public DeliveryOrderResponseDto serve(final UUID orderId) {
    final DeliveryOrder order = this.getDeliveryOrder(orderId);
    order.serve();
    return DeliveryOrderResponseDto.create(order);
  }

  @Transactional
  public DeliveryOrderResponseDto startDelivery(final UUID orderId) {
    final DeliveryOrder order = this.getDeliveryOrder(orderId);

    order.startDelivery();
    return DeliveryOrderResponseDto.create(order);
  }

  @Transactional
  public DeliveryOrderResponseDto completeDelivery(final UUID orderId) {
    final DeliveryOrder order = this.getDeliveryOrder(orderId);

    order.completeDelivery();
    return DeliveryOrderResponseDto.create(order);
  }

  @Transactional
  public DeliveryOrderResponseDto complete(final UUID orderId) {
    final DeliveryOrder order = this.getDeliveryOrder(orderId);
    order.complete();
    return DeliveryOrderResponseDto.create(order);
  }

  @Transactional(readOnly = true)
  public List<DeliveryOrderResponseDto> findAll() {
    return orderRepository.findAll().stream().map(DeliveryOrderResponseDto::create).toList();
  }

  private List<DeliveryOrderLineItem> createDeliveryOrderLineItems(
      final List<DeliveryOrderLineItemRequestDto> deliveryOrderLineItemRequestDtos) {

    if (Objects.isNull(deliveryOrderLineItemRequestDtos)) {
      throw new IllegalArgumentException();
    }

    final List<DeliveryOrderLineItem> deliveryOrderLineItems = new ArrayList<>();
    for (DeliveryOrderLineItemRequestDto deliveryOrderLineItemRequestDto :
        deliveryOrderLineItemRequestDtos) {
      final UUID menuId = deliveryOrderLineItemRequestDto.getMenuId();
      final BigDecimal price = deliveryOrderLineItemRequestDto.getPrice();
      final long quantity = deliveryOrderLineItemRequestDto.getQuantity();

      deliveryMenuClientInterface.validatorMenuCheckMethod(menuId, price);
      deliveryOrderLineItems.add(DeliveryOrderLineItem.createItem(quantity, menuId, price));
    }
    return deliveryOrderLineItems;
  }

  private DeliveryOrder getDeliveryOrder(UUID orderId) {
    return orderRepository.findById(orderId).orElseThrow(NoSuchElementException::new);
  }
}
