package com.isaaclevi.inventory.service;

import com.isaaclevi.inventory.model.AppUser;
import com.isaaclevi.inventory.model.ListPermission;
import com.isaaclevi.inventory.model.ShoppingList;
import com.isaaclevi.inventory.repository.AppUserRepository;
import com.isaaclevi.inventory.repository.ListPermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * List access rules: the owner implicitly holds every permission; other
 * users hold whatever the owner granted them (a permission row existing
 * at all implies view/membership).
 */
@Service
public class PermissionService {

    private final ListPermissionRepository permissions;
    private final AppUserRepository users;

    public PermissionService(ListPermissionRepository permissions, AppUserRepository users) {
        this.permissions = permissions;
        this.users = users;
    }

    public AppUser currentUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    public boolean isOwner(ShoppingList list, AppUser user) {
        return list.getOwner().getId().equals(user.getId());
    }

    public boolean canView(ShoppingList list, AppUser user) {
        return isOwner(list, user)
                || permissions.findByListIdAndUserId(list.getId(), user.getId()).isPresent();
    }

    public boolean canAdd(ShoppingList list, AppUser user) {
        return hasFlag(list, user, ListPermission::isCanAdd);
    }

    public boolean canEdit(ShoppingList list, AppUser user) {
        return hasFlag(list, user, ListPermission::isCanEdit);
    }

    public boolean canDelete(ShoppingList list, AppUser user) {
        return hasFlag(list, user, ListPermission::isCanDelete);
    }

    public void requireOwner(ShoppingList list, AppUser user) {
        if (!isOwner(list, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the list owner can do that");
        }
    }

    public void require(boolean allowed, String action) {
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to " + action + " on this list");
        }
    }

    private boolean hasFlag(ShoppingList list, AppUser user, Predicate<ListPermission> flag) {
        if (isOwner(list, user)) return true;
        Optional<ListPermission> permission = permissions.findByListIdAndUserId(list.getId(), user.getId());
        return permission.isPresent() && flag.test(permission.get());
    }
}
