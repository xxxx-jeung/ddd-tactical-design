package kitchenpos.eatinorders.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "orders")
@Entity
public class EatInOrder {

  @Column(name = "id", columnDefinition = "binary(16)")
  @Id
  private UUID id;

  @Column(name = "type", nullable = false, columnDefinition = "varchar(255)")
  @Enumerated(EnumType.STRING)
  private EatInOrderType type;

  @Column(name = "status", nullable = false, columnDefinition = "varchar(255)")
  @Enumerated(EnumType.STRING)
  private EatInOrderStatus status;

  @Embedded private EatInOrderLineItems orderLineItems;
  @Embedded private OrderDateTime orderDateTime;

  @ManyToOne
  @JoinColumn(
      name = "order_table_id",
      columnDefinition = "binary(16)",
      foreignKey = @ForeignKey(name = "fk_orders_to_order_table"))
  private EatInOrderTable orderTable;

  protected EatInOrder() {}

  protected EatInOrder(
      final UUID id,
      final List<EatInOrderLineItem> orderLineItems,
      final LocalDateTime orderDateTime,
      final EatInOrderTable orderTable) {
    this.id = id;
    this.type = EatInOrderType.EAT_IN;
    this.status = EatInOrderStatus.WAITING;
    this.orderLineItems = new EatInOrderLineItems(orderLineItems);
    this.orderDateTime = new OrderDateTime(orderDateTime);
    this.orderTable = orderTable;
  }

  public static EatInOrder createOrder(
      final List<EatInOrderLineItem> orderLineItems,
      final LocalDateTime orderDateTime,
      final EatInOrderTable orderTable) {
    return new EatInOrder(UUID.randomUUID(), orderLineItems, orderDateTime, orderTable);
  }

  public UUID getId() {
    return id;
  }

  public EatInOrderType getType() {
    return type;
  }

  public EatInOrderStatus getStatus() {
    return status;
  }

  public List<EatInOrderLineItem> getOrderLineItems() {
    return orderLineItems.getOrderLineItems();
  }

  public LocalDateTime getOrderDateTime() {
    return orderDateTime.getOrderDateTime();
  }

  public EatInOrderTable getOrderTable() {
    return orderTable;
  }
}
