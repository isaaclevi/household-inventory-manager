package com.isaaclevi.inventory.repository;

import com.isaaclevi.inventory.model.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {

    List<ItemType> findByCategoryIgnoreCase(String category);

    List<ItemType> findByNameContainingIgnoreCase(String name);
}
