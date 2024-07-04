package kitchenpos.eatinorders.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Table(name = "order_table")
@Entity
public class EatInOrderTable {
  @Column(name = "id", columnDefinition = "binary(16)")
  @Id
  private UUID id;

  @Embedded private Name name;

  @Embedded private NumberOfGuests numberOfGuests;

  @Embedded private Occupied occupied;

  protected EatInOrderTable() {}

  protected EatInOrderTable(
      final UUID id, final String name, final int numberOfGuests, final boolean occupied) {
    this.id = id;
    this.name = new Name(name);
    this.numberOfGuests = new NumberOfGuests(numberOfGuests);
    this.occupied = new Occupied(occupied);
  }

  public static EatInOrderTable createOrderTable(
      final String name, final int numberOfGuests, final boolean occupied) {
    return new EatInOrderTable(UUID.randomUUID(), name, numberOfGuests, occupied);
  }

  public void sit(int numberOfGuests) {
    if (this.occupied.isOccupied()) {
      throw new IllegalStateException();
    }

    this.validatorSitNumberOrGuests(numberOfGuests);

    this.numberOfGuests = new NumberOfGuests(numberOfGuests);
    this.occupied = new Occupied(true);
  }

  public void changeNumberOfGuests(final int numberOfGuests) {
    this.validatorSitTable();
    this.validatorSitNumberOrGuests(numberOfGuests);

    this.numberOfGuests = new NumberOfGuests(numberOfGuests);
  }

  public void clear() {
    this.validatorSitTable();
    this.validatorSitNumberOrGuests(this.numberOfGuests.getNumberOfGuests());

    this.numberOfGuests = new NumberOfGuests(0);
    this.occupied = new Occupied(false);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name.getName();
  }

  public int getNumberOfGuests() {
    return numberOfGuests.getNumberOfGuests();
  }

  public boolean getOccupied() {
    return occupied.isOccupied();
  }

  private void validatorSitTable() {
    if (!this.occupied.isOccupied()) {
      throw new IllegalStateException();
    }
  }

  private void validatorSitNumberOrGuests(int numberOfGuests) {
    if (numberOfGuests < 0) {
      throw new IllegalArgumentException();
    }
  }
}
