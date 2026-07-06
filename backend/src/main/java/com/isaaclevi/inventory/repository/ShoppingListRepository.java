package com.isaaclevi.inventory.repository;

import com.isaaclevi.inventory.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    /** Lists the user owns or is a member of (has a permission row on). */
    @Query("""
            select l from ShoppingList l
            where l.owner.id = :userId
               or exists (select p from ListPermission p where p.list = l and p.user.id = :userId)
            order by l.name""")
    List<ShoppingList> findAllAccessibleBy(@Param("userId") Long userId);

    boolean existsByNameIgnoreCaseAndOwnerId(String name, Long ownerId);
}
