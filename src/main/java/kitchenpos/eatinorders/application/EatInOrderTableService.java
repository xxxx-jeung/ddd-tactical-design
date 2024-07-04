package kitchenpos.eatinorders.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.eatinorders.domain.EatInOrderRepository;
import kitchenpos.eatinorders.domain.EatInOrderStatus;
import kitchenpos.eatinorders.domain.EatInOrderTable;
import kitchenpos.eatinorders.domain.EatInOrderTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EatInOrderTableService {
  private final EatInOrderTableRepository orderTableRepository;
  private final EatInOrderRepository orderRepository;

  public EatInOrderTableService(
      final EatInOrderTableRepository orderTableRepository,
      final EatInOrderRepository orderRepository) {
    this.orderTableRepository = orderTableRepository;
    this.orderRepository = orderRepository;
  }

  @Transactional
  public EatInOrderTableResponse create(final String name) {
    final EatInOrderTable orderTable = EatInOrderTable.createOrderTable(name, 0, false);
    orderTableRepository.save(orderTable);
    return EatInOrderTableResponse.create(orderTable);
  }

  @Transactional
  public EatInOrderTableResponse sit(final UUID orderTableId, final int numberOfGuests) {
    final EatInOrderTable orderTable = this.getEatInOrderTable(orderTableId);
    orderTable.sit(numberOfGuests);
    return EatInOrderTableResponse.create(orderTable);
  }

  @Transactional
  public EatInOrderTableResponse clear(final UUID orderTableId) {
    final EatInOrderTable orderTable = this.getEatInOrderTable(orderTableId);

    this.findOrderStatus(orderTable);

    orderTable.clear();
    return EatInOrderTableResponse.create(orderTable);
  }

  @Transactional
  public EatInOrderTableResponse changeNumberOfGuests(
      final UUID orderTableId, final int numberOfGuests) {
    final EatInOrderTable orderTable = this.getEatInOrderTable(orderTableId);
    orderTable.changeNumberOfGuests(numberOfGuests);
    return EatInOrderTableResponse.create(orderTable);
  }

  @Transactional(readOnly = true)
  public List<EatInOrderTableResponse> findAll() {
    final List<EatInOrderTable> eatInOrderTables = orderTableRepository.findAll();
    return eatInOrderTables.stream().map(EatInOrderTableResponse::create).toList();
  }

  private void findOrderStatus(EatInOrderTable orderTable) {
    if (orderRepository.existsByOrderTableAndStatusNot(orderTable, EatInOrderStatus.COMPLETED)) {
      throw new IllegalStateException();
    }
  }

  private EatInOrderTable getEatInOrderTable(UUID orderTableId) {
    return orderTableRepository.findById(orderTableId).orElseThrow(NoSuchElementException::new);
  }
}
