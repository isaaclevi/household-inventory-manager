package com.isaaclevi.inventory.repository;

import com.isaaclevi.inventory.model.ItemInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ItemInstanceRepository extends JpaRepository<ItemInstance, Long> {

    List<ItemInstance> findByConsumedFalseOrderByExpirationDateAsc();

    List<ItemInstance> findByConsumedFalseAndExpirationDateLessThanEqualOrderByExpirationDateAsc(LocalDate cutoff);

    List<ItemInstance> findByItemTypeIdAndConsumedFalse(Long itemTypeId);
}
