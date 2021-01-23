package com.sandbox.model;

import com.sandbox.exceptions.InvalidIdException;
import com.sandbox.model.constants.ItemType;

import java.util.Locale;

public class Item {
    private final String id;
    private final ItemType itemType;
    private final String name;

    public Item(String id, ItemType itemType, String name) {
        if (id == null || id.isBlank()) {
            throw new InvalidIdException("Item ID can't be empty.");
        } else if (id.length() > 32) {
            throw new InvalidIdException("Item ID can't be longer than 32 characters.");
        } else if (!id.matches("[A-Za-z0-9]{1,32}")) {
            throw new InvalidIdException("Item ID may only contain alphanumeric characters.");
        }
        this.id = id.toUpperCase(Locale.ROOT);
        this.itemType = itemType;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return id.equalsIgnoreCase(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

}
