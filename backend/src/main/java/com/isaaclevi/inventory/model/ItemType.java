package com.isaaclevi.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * A kind of product ("2% Milk", "Organic Eggs"). Different variants are
 * different ItemTypes; the physical containers in the house are ItemInstances.
 */
@Entity
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String brand;

    @NotBlank
    private String category;

    private String defaultUnit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDefaultUnit() { return defaultUnit; }
    public void setDefaultUnit(String defaultUnit) { this.defaultUnit = defaultUnit; }
}
