package com.isaaclevi.inventory.web;

import com.isaaclevi.inventory.model.ShoppingListItem;
import com.isaaclevi.inventory.repository.ShoppingListItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListItemRepository repository;

    public ShoppingListController(ShoppingListItemRepository repository) {
        this.repository = repository;
    }

    /** Open (not yet purchased) items, grouped by category then name. */
    @GetMapping
    public List<ShoppingListItem> openItems() {
        return repository.findByPurchasedFalseOrderByCategoryAscNameAsc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItem create(@Valid @RequestBody ShoppingListItem item) {
        item.setId(null);
        return repository.save(item);
    }

    @PostMapping("/{id}/purchase")
    public ShoppingListItem purchase(@PathVariable Long id) {
        ShoppingListItem item = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        item.setPurchased(true);
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
