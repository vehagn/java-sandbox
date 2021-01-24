package com.sandbox.model;

import com.sandbox.exceptions.InvalidIdException;
import com.sandbox.model.items.animals.Dog;
import com.sandbox.model.items.solids.Ball;
import com.sandbox.model.items.solids.Box;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    @Test
    void testConstructor() {
        Exception exception;

        exception = assertThrows(InvalidIdException.class, () -> new Ball(null, "Red", 1.0),
                "Item ID can't be null.");
        assertTrue(exception.getMessage().contains("can't be empty"));

        exception = assertThrows(InvalidIdException.class, () -> new Ball("  ", "Blue", 1.0),
                "Item ID can't be blank.");
        assertTrue(exception.getMessage().contains("can't be empty"));

        final String tooLong = "a".repeat(33);
        exception = assertThrows(InvalidIdException.class, () -> new Box(tooLong, "Brown", 1.0, 1.0, 1.0),
                "Max Item ID is 32.");
        assertTrue(exception.getMessage().contains("can't be longer than 32 char"));

        exception = assertThrows(InvalidIdException.class, () -> new Dog("æ"),
                "Item ID can only contain alphanumerical characters.");
        assertTrue(exception.getMessage().contains("may only contain alphanumeric"));

        assertDoesNotThrow(() -> new Dog("ABC123"));
    }

}