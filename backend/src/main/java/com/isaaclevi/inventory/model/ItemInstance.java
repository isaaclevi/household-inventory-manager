package com.isaaclevi.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * One physical unit in the house (one milk carton). Two cartons of the same
 * product with different expiration dates are two ItemInstances of one ItemType.
 */
@Entity
public class ItemInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    private ItemType itemType;

    @Positive
    private double quantity = 1;

    /** Null for items without a printed date (produce, bulk). */
    private LocalDate expirationDate;

    private LocalDateTime addedAt = LocalDateTime.now();

    /** Placeholder until Spring Security is wired in (Phase 1, auth step). */
    private String addedBy;

    private boolean consumed = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }
    public boolean isConsumed() { return consumed; }
    public void setConsumed(boolean consumed) { this.consumed = consumed; }
}
