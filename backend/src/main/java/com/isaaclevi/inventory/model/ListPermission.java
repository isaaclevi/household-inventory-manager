package com.isaaclevi.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * A member's rights on one list. The owner is not stored here — owners
 * implicitly hold every permission. Membership (a row existing) implies view.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "user_id"}))
public class ListPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "list_id")
    private ShoppingList list;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    private boolean canAdd;
    private boolean canEdit;
    private boolean canDelete;

    private LocalDateTime grantedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ShoppingList getList() { return list; }
    public void setList(ShoppingList list) { this.list = list; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public boolean isCanAdd() { return canAdd; }
    public void setCanAdd(boolean canAdd) { this.canAdd = canAdd; }
    public boolean isCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }
    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }
}
