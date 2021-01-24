package com.sandbox.model.items.solids;

import java.math.BigDecimal;

public class Ball extends Solid {
    private final double radius;

    public Ball(final String id, final String color, final double radius) {
        super(id, color, 0, BigDecimal.valueOf(2)
                .multiply(BigDecimal.valueOf(Math.PI)
                        .multiply(BigDecimal.valueOf(radius).pow(2))));
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ball)) return false;
        if (!super.equals(o)) return false;

        Ball ball = (Ball) o;

        return Double.compare(ball.radius, radius) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(radius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getRadius() {
        return radius;
    }
}
