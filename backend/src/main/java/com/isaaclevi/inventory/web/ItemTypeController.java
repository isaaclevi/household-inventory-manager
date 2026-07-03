package com.isaaclevi.inventory.web;

import com.isaaclevi.inventory.model.ItemType;
import com.isaaclevi.inventory.repository.ItemTypeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/item-types")
public class ItemTypeController {

    private final ItemTypeRepository repository;

    public ItemTypeController(ItemTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ItemType> all(@RequestParam(required = false) String category,
                              @RequestParam(required = false) String search) {
        if (category != null) return repository.findByCategoryIgnoreCase(category);
        if (search != null) return repository.findByNameContainingIgnoreCase(search);
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ItemType one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemType create(@Valid @RequestBody ItemType itemType) {
        itemType.setId(null);
        return repository.save(itemType);
    }

    @PutMapping("/{id}")
    public ItemType update(@PathVariable Long id, @Valid @RequestBody ItemType itemType) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        itemType.setId(id);
        return repository.save(itemType);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
