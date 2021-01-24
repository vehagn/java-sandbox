package com.sandbox.model.items.solids;

import com.sandbox.model.items.Item;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Solid extends Item {

    private final String color;
    private final int sides;
    private final BigDecimal volume;

    protected Solid(String id, String color, int sides, BigDecimal volume) {
        super(id);
        this.color = color;
        this.sides = sides;
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solid)) return false;
        if (!super.equals(o)) return false;

        Solid solid = (Solid) o;

        if (sides != solid.sides) return false;
        if (!Objects.equals(color, solid.color)) return false;
        return volume.equals(solid.volume);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + sides;
        result = 31 * result + volume.hashCode();
        return result;
    }
}