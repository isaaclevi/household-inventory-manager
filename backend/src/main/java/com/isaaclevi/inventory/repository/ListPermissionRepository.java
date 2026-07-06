package com.isaaclevi.inventory.repository;

import com.isaaclevi.inventory.model.ListPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListPermissionRepository extends JpaRepository<ListPermission, Long> {

    Optional<ListPermission> findByListIdAndUserId(Long listId, Long userId);

    List<ListPermission> findByListId(Long listId);
}
