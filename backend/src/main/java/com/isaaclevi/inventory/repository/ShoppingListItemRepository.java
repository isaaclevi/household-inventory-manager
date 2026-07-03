package com.isaaclevi.inventory.repository;

import com.isaaclevi.inventory.model.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    List<ShoppingListItem> findByPurchasedFalseOrderByCategoryAscNameAsc();
}
