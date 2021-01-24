package com.sandbox.model.items;

import com.sandbox.model.items.animals.Animal;
import com.sandbox.model.items.animals.Dog;
import com.sandbox.model.items.solids.Ball;
import com.sandbox.model.items.solids.Box;
import com.sandbox.model.items.solids.Solid;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ItemClass {
    BALL(Ball.class),
    BOX(Box.class),
    DOG(Dog.class);

    private static final Map<Class<? extends Item>, ItemClass> ENUM_MAP =
            Stream.of(ItemClass.values()).collect(
                    Collectors.toUnmodifiableMap(
                            t -> t.type,
                            t -> t));
    private final Class<? extends Item> type;

    <T extends Item> ItemClass(final Class<T> type) {
        this.type = type;
    }

    public static Set<Class<? extends Item>> getAllClasses() {
        return Arrays.stream(ItemClass.values())
                .map(ItemClass::getItemClass)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String toString() {
        return this.type.getSimpleName();
    }

    public Class<? extends Item> getItemClass() {
        return this.type;
    }

    public Class<? extends Item> getParentClass() {
        return switch (this) {
            case BALL, BOX -> Solid.class;
            case DOG -> Animal.class;
        };
    }
}
