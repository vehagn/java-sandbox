package com.sandbox.model.items;

import com.sandbox.exceptions.InvalidIdException;
import com.sandbox.model.Entity;

import java.util.Locale;

public abstract class Item implements Entity {
    private final String id;

    protected Item(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidIdException("Item ID can't be empty.");
        } else if (id.length() > 32) {
            throw new InvalidIdException("Item ID can't be longer than 32 characters.");
        } else if (!id.matches("[A-Za-z0-9]{1,32}")) {
            throw new InvalidIdException("Item ID may only contain alphanumeric characters.");
        }
        this.id = id.toUpperCase(Locale.ROOT);
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
}
