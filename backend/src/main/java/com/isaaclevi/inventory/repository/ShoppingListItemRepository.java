package com.isaaclevi.inventory.repository;

import com.isaaclevi.inventory.model.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, UUID> {

    List<ShoppingListItem> findByListIdAndDeletedFalseOrderByCategoryAscNameAsc(Long listId);

    /** Includes tombstones — used by sync so deletions propagate. */
    List<ShoppingListItem> findByListId(Long listId);
}
