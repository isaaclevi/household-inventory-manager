package com.isaaclevi.inventory.web;

import com.isaaclevi.inventory.model.ItemInstance;
import com.isaaclevi.inventory.repository.ItemInstanceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemInstanceController {

    private final ItemInstanceRepository repository;

    public ItemInstanceController(ItemInstanceRepository repository) {
        this.repository = repository;
    }

    /** Everything currently in the house, soonest expiration first. */
    @GetMapping
    public List<ItemInstance> inventory() {
        return repository.findByConsumedFalseOrderByExpirationDateAsc();
    }

    /** Items expiring within {days} days (default 3), including already expired. */
    @GetMapping("/expiring-soon")
    public List<ItemInstance> expiringSoon(@RequestParam(defaultValue = "3") int days) {
        return repository.findByConsumedFalseAndExpirationDateLessThanEqualOrderByExpirationDateAsc(
                LocalDate.now().plusDays(days));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemInstance create(@Valid @RequestBody ItemInstance instance) {
        instance.setId(null);
        return repository.save(instance);
    }

    @PutMapping("/{id}")
    public ItemInstance update(@PathVariable Long id, @Valid @RequestBody ItemInstance instance) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        instance.setId(id);
        return repository.save(instance);
    }

    /** Mark an item as used up / thrown out without deleting its history. */
    @PostMapping("/{id}/consume")
    public ItemInstance consume(@PathVariable Long id) {
        ItemInstance instance = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        instance.setConsumed(true);
        return repository.save(instance);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
