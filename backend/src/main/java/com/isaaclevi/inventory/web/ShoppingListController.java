package com.isaaclevi.inventory.web;

import com.isaaclevi.inventory.model.*;
import com.isaaclevi.inventory.repository.*;
import com.isaaclevi.inventory.service.PermissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists")
public class ShoppingListController {

    public record CreateListRequest(@NotBlank String name) {}

    public record PermissionRequest(@NotBlank String username,
                                    boolean canAdd, boolean canEdit, boolean canDelete) {}

    public record PermissionView(String username, String displayName,
                                 boolean canAdd, boolean canEdit, boolean canDelete) {}

    public record MyPermissions(boolean owner, boolean canAdd, boolean canEdit, boolean canDelete) {}

    private final ShoppingListRepository lists;
    private final ShoppingListItemRepository items;
    private final ListPermissionRepository permissions;
    private final AppUserRepository users;
    private final PermissionService access;

    public ShoppingListController(ShoppingListRepository lists, ShoppingListItemRepository items,
                                  ListPermissionRepository permissions, AppUserRepository users,
                                  PermissionService access) {
        this.lists = lists;
        this.items = items;
        this.permissions = permissions;
        this.users = users;
        this.access = access;
    }

    // --- Lists ---

    @GetMapping
    public List<ShoppingList> myLists(Principal principal) {
        AppUser user = access.currentUser(principal.getName());
        return lists.findAllAccessibleBy(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingList create(@Valid @RequestBody CreateListRequest request, Principal principal) {
        AppUser user = access.currentUser(principal.getName());
        if (lists.existsByNameIgnoreCaseAndOwnerId(request.name().trim(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already own a list with that name");
        }
        ShoppingList list = new ShoppingList();
        list.setName(request.name().trim());
        list.setOwner(user);
        return lists.save(list);
    }

    @DeleteMapping("/{listId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteList(@PathVariable Long listId, Principal principal) {
        ShoppingList list = getList(listId);
        access.requireOwner(list, access.currentUser(principal.getName()));
        items.deleteAll(items.findByListId(listId));
        permissions.deleteAll(permissions.findByListId(listId));
        lists.delete(list);
    }

    /** What the calling user may do on this list — drives which buttons the UI shows. */
    @GetMapping("/{listId}/my-permissions")
    public MyPermissions myPermissions(@PathVariable Long listId, Principal principal) {
        ShoppingList list = getList(listId);
        AppUser user = access.currentUser(principal.getName());
        access.require(access.canView(list, user), "view");
        return new MyPermissions(access.isOwner(list, user),
                access.canAdd(list, user), access.canEdit(list, user), access.canDelete(list, user));
    }

    // --- Permissions (owner only) ---

    @GetMapping("/{listId}/permissions")
    public List<PermissionView> listPermissions(@PathVariable Long listId, Principal principal) {
        ShoppingList list = getList(listId);
        access.requireOwner(list, access.currentUser(principal.getName()));
        return permissions.findByListId(listId).stream()
                .map(p -> new PermissionView(p.getUser().getUsername(), p.getUser().getDisplayName(),
                        p.isCanAdd(), p.isCanEdit(), p.isCanDelete()))
                .toList();
    }

    @PostMapping("/{listId}/permissions")
    public PermissionView grant(@PathVariable Long listId,
                                @Valid @RequestBody PermissionRequest request, Principal principal) {
        ShoppingList list = getList(listId);
        AppUser owner = access.currentUser(principal.getName());
        access.requireOwner(list, owner);

        AppUser member = users.findByUsername(request.username().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user"));
        if (member.getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The owner already has every permission");
        }

        ListPermission permission = permissions.findByListIdAndUserId(listId, member.getId())
                .orElseGet(() -> {
                    ListPermission p = new ListPermission();
                    p.setList(list);
                    p.setUser(member);
                    return p;
                });
        permission.setCanAdd(request.canAdd());
        permission.setCanEdit(request.canEdit());
        permission.setCanDelete(request.canDelete());
        permissions.save(permission);
        return new PermissionView(member.getUsername(), member.getDisplayName(),
                permission.isCanAdd(), permission.isCanEdit(), permission.isCanDelete());
    }

    @DeleteMapping("/{listId}/permissions/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable Long listId, @PathVariable String username, Principal principal) {
        ShoppingList list = getList(listId);
        access.requireOwner(list, access.currentUser(principal.getName()));
        AppUser member = users.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user"));
        permissions.findByListIdAndUserId(listId, member.getId()).ifPresent(permissions::delete);
    }

    // --- Items ---

    @GetMapping("/{listId}/items")
    public List<ShoppingListItem> listItems(@PathVariable Long listId,
                                            @RequestParam(defaultValue = "false") boolean includeDeleted,
                                            Principal principal) {
        ShoppingList list = getList(listId);
        AppUser user = access.currentUser(principal.getName());
        access.require(access.canView(list, user), "view items");
        return includeDeleted
                ? items.findByListId(listId)
                : items.findByListIdAndDeletedFalseOrderByCategoryAscNameAsc(listId);
    }

    @PostMapping("/{listId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItem addItem(@PathVariable Long listId,
                                    @Valid @RequestBody ShoppingListItem item, Principal principal) {
        ShoppingList list = getList(listId);
        AppUser user = access.currentUser(principal.getName());
        access.require(access.canAdd(list, user), "add items");

        if (item.getId() == null) item.setId(UUID.randomUUID()); // offline devices send their own UUID
        item.setList(list);
        item.setAddedBy(user.getUsername());
        item.setDeleted(false);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return items.save(item);
    }

    @PutMapping("/{listId}/items/{itemId}")
    public ShoppingListItem editItem(@PathVariable Long listId, @PathVariable UUID itemId,
                                     @Valid @RequestBody ShoppingListItem changes, Principal principal) {
        ShoppingList list = getList(listId);
        AppUser user = access.currentUser(principal.getName());
        access.require(access.canEdit(list, user), "edit items");

        ShoppingListItem item = getItem(list, itemId);
        item.setName(changes.getName());
        item.setCategory(changes.getCategory());
        item.setQuantity(changes.getQuantity());
        item.setPurchased(changes.isPurchased());
        item.setUpdatedAt(LocalDateTime.now());
        return items.save(item);
    }

    @PostMapping("/{listId}/items/{itemId}/purchase")
    public ShoppingListItem purchase(@PathVariable Long listId, @PathVariable UUID itemId, Principal principal) {
        ShoppingList list = getList(listId);
        AppUser user = access.currentUser(principal.getName());
        access.require(access.canEdit(list, user), "check off items");

        ShoppingListItem item = getItem(list, itemId);
        item.setPurchased(true);
        item.setUpdatedAt(LocalDateTime.now());
        return items.save(item);
    }

    /** Tombstone, not a hard delete — deletions must propagate through sync. */
    @DeleteMapping("/{listId}/items/{itemId}")
    public ShoppingListItem removeItem(@PathVariable Long listId, @PathVariable UUID itemId, Principal principal) {
        ShoppingList list = getList(listId);
        AppUser user = access.currentUser(principal.getName());
        access.require(access.canDelete(list, user), "delete items");

        ShoppingListItem item = getItem(list, itemId);
        item.setDeleted(true);
        item.setUpdatedAt(LocalDateTime.now());
        return items.save(item);
    }

    private ShoppingList getList(Long listId) {
        return lists.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such list"));
    }

    private ShoppingListItem getItem(ShoppingList list, UUID itemId) {
        return items.findById(itemId)
                .filter(i -> i.getList().getId().equals(list.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such item on this list"));
    }
}
