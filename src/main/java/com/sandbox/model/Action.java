package com.sandbox.model;

import java.time.Instant;
import java.util.UUID;

public class Action {
    private final UUID id;

    private final String description;
    private final Integer cost;
    private final Instant performedDateTime;
    private final String itemId;

    public Action(String itemId, String description, Integer cost, Instant performedDateTime) {
        this.id = UUID.randomUUID();
        this.itemId = itemId;
        this.description = description;
        this.cost = cost;
        this.performedDateTime = performedDateTime;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCost() {
        return cost;
    }

    public Instant getPerformedDateTime() {
        return performedDateTime;
    }

    public String getItemId() {
        return itemId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        return id.equals(action.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
