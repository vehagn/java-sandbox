package com.sandbox.model.constants;

public enum ItemType {
    A("Type A"),
    B("Type B"),
    C("Type C");

    private final String type;

    ItemType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
