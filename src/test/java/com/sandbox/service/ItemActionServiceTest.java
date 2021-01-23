package com.sandbox.service;

import com.sandbox.exceptions.ActionNotFoundException;
import com.sandbox.exceptions.ItemIdAlreadyRegisteredException;
import com.sandbox.exceptions.ItemNotFoundException;
import com.sandbox.model.Action;
import com.sandbox.model.Item;
import com.sandbox.model.constants.ItemType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemActionServiceTest {
    final Item itemA1 = new Item("A123", ItemType.A, "Item A1");
    final Item itemA2 = new Item("A111", ItemType.A, "Item A2");

    final Item itemB1 = new Item("B123", ItemType.B, "Item B1");
    final Item itemB2 = new Item("B456", ItemType.B, "Item B2");

    final Item itemC1 = new Item("C111", ItemType.C, "Item C1");
    final Item itemC2 = new Item("C222", ItemType.C, "Item C2");
    final Item itemC3 = new Item("C333", ItemType.C, "Item C3");

    @Test
    void testRegisterItem() {
        final ItemActionService itemActionService = new ItemActionService();
        Exception exception;

        final Set<Item> items = new HashSet<>() {{
            add(itemA1);
            add(itemA2);
        }};

        assertDoesNotThrow(() -> itemActionService.registerItem(itemA2),
                "We should be able to register an item with an Item object.");
        assertDoesNotThrow(() -> itemActionService.registerItem(itemA1.getId(), itemA1.getType(), itemA1.getName()),
                "We should be able to register an item by its constituents.");

        assertEquals(items, itemActionService.getAllItems());

        exception = assertThrows(ItemIdAlreadyRegisteredException.class,
                () -> itemActionService.registerItem(itemA1.getId(), itemB1.getType(), itemB1.getName()),
                "We should not be able to register an item with the same Id");
        assertTrue(exception.getMessage().contains("Item ID already registered."));
    }

    @Test
    void testGetItemById() {
        final ItemActionService itemActionService = new ItemActionService();
        assertThrows(ItemNotFoundException.class, () -> itemActionService.getItemById(itemA1.getId()));

        itemActionService.registerItem(itemA1);
        assertEquals(itemA1, itemActionService.getItemById(itemA1.getId()));
    }

    @Test
    void testGetActionById() {
        final ItemActionService itemActionService = new ItemActionService();
        UUID uuidNull = new UUID(0, 0);
        assertThrows(ActionNotFoundException.class, () -> itemActionService.getActionById(uuidNull));

        final String actionDescription = "Upgrade";
        final int actionCost = 9001;
        final Instant actionPerformed = Instant.now();

        itemActionService.registerItem(itemC1);
        final UUID actionId = itemActionService.registerActionOnItem(itemC1.getId(), actionDescription, actionCost, actionPerformed);
        final Action action = itemActionService.getActionById(actionId);

        assertEquals(actionId, action.getId());
        assertEquals(actionDescription, action.getDescription());
        assertEquals(actionCost, action.getCost());
        assertEquals(actionPerformed, action.getPerformedDateTime());
    }

    @Test
    void testRegisterActionOnItem() {
        final ItemActionService itemActionService = new ItemActionService();
        final String itemA1Id = itemA1.getId();

        assertThrows(ItemNotFoundException.class, () -> itemActionService.registerActionOnItem(itemA1Id, "Throw", 5000, Instant.now()));

        itemActionService.registerItem(itemA1.getId(), itemA1.getType(), itemA1.getName());
        final UUID actionId = itemActionService.registerActionOnItem(itemA1Id, "Throw", 5000, Instant.now());
        final Action action = itemActionService.getActionById(actionId);

        final Set<Action> actions = new HashSet<>() {{
            add(action);
        }};
        assertEquals(actions, itemActionService.getAllActions());
    }

    @Test
    void testGetActionOnItemByActionId() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(itemC1.getId(), itemC1.getType(), itemC1.getName());
        final UUID actionId = itemActionService.registerActionOnItem(itemC1.getId(), "Change", 9001, Instant.now());
        final Action action = itemActionService.getActionById(actionId);

        HashMap<Action, Item> repairDetails = new HashMap<>() {{
            put(action, itemC1);
        }};
        assertEquals(repairDetails, itemActionService.getActionOnItemByActionId(action.getId()));
    }

    @Test
    void testGetActionsOfItemTypeSortedByPerformedDate() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(itemC1);
        itemActionService.registerItem(itemC2);

        itemActionService.registerActionOnItem(itemC1.getId(), "Jump", 3, Instant.now()); // Newest
        itemActionService.registerActionOnItem(itemC2.getId(), "Lift", 1, Instant.now().minus(Duration.of(20, ChronoUnit.MINUTES))); // Oldest
        itemActionService.registerActionOnItem(itemC2.getId(), "Calm", 2, Instant.now().minus(Duration.of(10, ChronoUnit.MINUTES)));

        itemActionService.registerItem(itemA1);
        itemActionService.registerActionOnItem(itemA1.getId(), "Tires", 42, Instant.now());

        List<Action> repairList = itemActionService.getActionsOnItemTypeSortedByPerformedDate(ItemType.C);

        assertEquals(3, repairList.size(), "We should get 3 actions of ItemType C");
        assertEquals(1, repairList.get(0).getCost(), "Oldest action has cost 1");
        assertEquals(2, repairList.get(1).getCost());
        assertEquals(3, repairList.get(2).getCost(), "Newest action has cost 3");

        assertEquals(1, itemActionService.getActionsOnItemTypeSortedByPerformedDate(ItemType.A).size());
        assertEquals(0, itemActionService.getActionsOnItemTypeSortedByPerformedDate(ItemType.B).size());
    }

    @Test
    void testGetItemIdsWithTotalActionCost() {
        final ItemActionService itemActionService = new ItemActionService();
        itemActionService.registerItem(itemC1);
        itemActionService.registerItem(itemC2);

        itemActionService.registerActionOnItem(itemC1.getId(), "Wink", 100, Instant.now());
        itemActionService.registerActionOnItem(itemC2.getId(), "Lick", 100, Instant.now());
        itemActionService.registerActionOnItem(itemC2.getId(), "Kick", 100, Instant.now());

        HashMap<String, Integer> vehicleRepairCost = new HashMap<>() {{
            put(itemC1.getId(), 100);
            put(itemC2.getId(), 200);
        }};

        assertEquals(vehicleRepairCost.get(itemC1.getId()), itemActionService.getItemIdsWithTotalActionCost().get(itemC1.getId()));
        assertEquals(vehicleRepairCost.get(itemC2.getId()), itemActionService.getItemIdsWithTotalActionCost().get(itemC2.getId()));
    }


    @Test
    void testGetItemsWithHighestTotalActionCostByItemType() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(itemB1);
        itemActionService.registerItem(itemB2);

        itemActionService.registerActionOnItem(itemB1.getId(), "Meet", 1000, Instant.now());
        itemActionService.registerActionOnItem(itemB1.getId(), "Greet", 1000, Instant.now());
        itemActionService.registerActionOnItem(itemB2.getId(), "Feet", 1000, Instant.now());

        assertEquals(Collections.singletonMap(itemB1, 2000), itemActionService.getItemsWithHighestTotalActionCostByItemType(ItemType.B),
                "Item B1 should the highest action cost.");

        itemActionService.registerItem(itemC1);
        itemActionService.registerItem(itemC2);
        itemActionService.registerItem(itemC3);

        itemActionService.registerActionOnItem(itemC1.getId(), "Sheet", 100, Instant.now());
        itemActionService.registerActionOnItem(itemC2.getId(), "Leet", 100, Instant.now());
        itemActionService.registerActionOnItem(itemC3.getId(), "Fleet", 0, Instant.now());

        HashMap<Item, Integer> pickupARepair = new HashMap<>() {{
            put(itemC1, 100);
            put(itemC2, 100);
        }};
        assertEquals(pickupARepair, itemActionService.getItemsWithHighestTotalActionCostByItemType(ItemType.C),
                "Both itemC1 and itemC2 have equal and the highest total action cost.");

        assertNull(itemActionService.getItemsWithHighestTotalActionCostByItemType(ItemType.A),
                "No actions for ItemType A should be registered.");
    }
}