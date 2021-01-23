package com.sandbox.model;

import com.sandbox.exceptions.InvalidIdException;
import com.sandbox.model.constants.ItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    @Test
    void testConstructor() {
        Exception exception;

        exception = assertThrows(InvalidIdException.class, () -> new Item(null, ItemType.A, "Name"),
                "Item ID can't be null.");
        assertTrue(exception.getMessage().contains("can't be empty"));

        exception = assertThrows(InvalidIdException.class, () -> new Item("  ", ItemType.A, "Name"),
                "Item ID can't be blank.");
        assertTrue(exception.getMessage().contains("can't be empty"));

        final String tooLong = "a".repeat(33);
        exception = assertThrows(InvalidIdException.class, () -> new Item(tooLong, ItemType.A, "Name"),
                "Max Item ID is 32.");
        assertTrue(exception.getMessage().contains("can't be longer than 32 char"));

        exception = assertThrows(InvalidIdException.class, () -> new Item("æ", ItemType.A, "Name"),
                "Item ID can only contain alphanumerical characters.");
        assertTrue(exception.getMessage().contains("may only contain alphanumeric"));

        assertDoesNotThrow(() -> new Item("ABC123", ItemType.A, "Correct item."));
    }

    @Test
    void testEquals() {
        assertEquals(
                new Item("abc123", ItemType.A, "Lowercase"),
                new Item("ABC123", ItemType.B, "Uppercase"),
                "Items should be equal as long as their IDs are equal ignoring case.");

    }

}