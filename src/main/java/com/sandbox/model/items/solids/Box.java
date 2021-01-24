package com.sandbox.model.items.solids;

import java.math.BigDecimal;

public class Box extends Solid {
    private final double height;
    private final double depth;
    private final double width;

    public Box(String id, String color, double height, double depth, double width) {
        super(id, color, 6,
                BigDecimal.valueOf(height)
                        .multiply(BigDecimal.valueOf(depth)
                                .multiply(BigDecimal.valueOf(width))));
        this.height = height;
        this.depth = depth;
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;
        if (!super.equals(o)) return false;

        Box box = (Box) o;

        if (Double.compare(box.height, height) != 0) return false;
        if (Double.compare(box.depth, depth) != 0) return false;
        return Double.compare(box.width, width) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(depth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(width);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getDepth() {
        return depth;
    }

    public double getWidth() {
        return width;
    }
}
