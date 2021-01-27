package com.sandbox.service;

import com.sandbox.exceptions.ActionNotFoundException;
import com.sandbox.exceptions.ItemIdAlreadyRegisteredException;
import com.sandbox.exceptions.ItemNotFoundException;
import com.sandbox.model.Action;
import com.sandbox.model.items.Item;
import com.sandbox.model.items.ItemClass;
import com.sandbox.model.items.animals.Dog;
import com.sandbox.model.items.solids.Ball;
import com.sandbox.model.items.solids.Box;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemActionServiceTest {
    final Ball ballA = new Ball("Ball123", "Red", 1.0);
    final Ball ballB = new Ball("Ball111", "Blue", 2.0);

    final Box boxA = new Box("Box123", "Brown", 1.0, 2.0, 3.0);
    final Box boxB = new Box("Box456", "Mauve", 1.0, 2.0, 3.0);

    final Dog dogA = new Dog("Dog111");
    final Dog dogB = new Dog("Dog222");
    final Dog dogC = new Dog("Dog333");

    @Test
    void registerItem() {
        final ItemActionService itemActionService = new ItemActionService();
        Exception exception;

        final Set<Item> items = new HashSet<>() {{
            add(ballA);
            add(ballB);
        }};

        assertDoesNotThrow(() -> itemActionService.registerItem(ballA),
                "We should be able to register an item with an Item object.");
        itemActionService.registerItem(ballB);

        assertEquals(items, itemActionService.getAllItems());

        exception = assertThrows(ItemIdAlreadyRegisteredException.class,
                () -> itemActionService.registerItem(ballA),
                "We should not be able to register an item with the same Id");
        assertTrue(exception.getMessage().contains("Item ID already registered."));
    }

    @Test
    void getItemById() {
        final ItemActionService itemActionService = new ItemActionService();
        final String id = ballA.getId();
        assertThrows(ItemNotFoundException.class, () -> itemActionService.getItemById(id));

        itemActionService.registerItem(ballA);
        assertEquals(ballA, itemActionService.getItemById(ballA.getId()));
    }

    @Test
    void getActionById() {
        final ItemActionService itemActionService = new ItemActionService();
        UUID uuidNull = new UUID(0, 0);
        assertThrows(ActionNotFoundException.class, () -> itemActionService.getActionById(uuidNull));

        final String actionDescription = "Upgrade";
        final int actionCost = 9001;
        final Instant actionPerformed = Instant.now();

        itemActionService.registerItem(dogA);
        final UUID actionId = itemActionService.registerActionOnItem(dogA.getId(), actionDescription, actionCost, actionPerformed);
        final Action action = itemActionService.getActionById(actionId);

        assertEquals(actionId, action.getId());
        assertEquals(actionDescription, action.getDescription());
        assertEquals(actionCost, action.getCost());
        assertEquals(actionPerformed, action.getPerformedDateTime());
    }

    @Test
    void registerActionOnItem() {
        final ItemActionService itemActionService = new ItemActionService();
        final String ballAId = ballA.getId();
        final Instant now = Instant.now();

        assertThrows(ItemNotFoundException.class, () -> itemActionService.registerActionOnItem(ballAId, "Throw", 5000, now));

        itemActionService.registerItem(ballA);
        final UUID actionId = itemActionService.registerActionOnItem(ballAId, "Throw", 5000, Instant.now());
        final Action action = itemActionService.getActionById(actionId);

        final Set<Action> actions = new HashSet<>() {{
            add(action);
        }};
        assertEquals(actions, itemActionService.getAllActions());
    }

    @Test
    void getActionOnItemByActionId() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(dogA);
        final UUID actionId = itemActionService.registerActionOnItem(dogA.getId(), "Change", 9001, Instant.now());
        final Action action = itemActionService.getActionById(actionId);

        assertEquals(Collections.singletonMap(action, dogA), itemActionService.getActionOnItemByActionId(action.getId()));
    }

    @Test
    void getActionsOfItemTypeSortedByPerformedDate() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(dogA);
        itemActionService.registerItem(dogB);

        itemActionService.registerActionOnItem(dogA.getId(), "Jump", 3, Instant.now()); // Newest
        itemActionService.registerActionOnItem(dogB.getId(), "Lift", 1, Instant.now().minus(Duration.of(20, ChronoUnit.MINUTES))); // Oldest
        itemActionService.registerActionOnItem(dogB.getId(), "Calm", 2, Instant.now().minus(Duration.of(10, ChronoUnit.MINUTES)));

        itemActionService.registerItem(ballA);
        itemActionService.registerActionOnItem(ballA.getId(), "Tires", 42, Instant.now());

        List<Action> repairList = itemActionService.getActionsOnItemTypeSortedByPerformedDate(ItemClass.DOG);

        assertEquals(3, repairList.size(), "We should get 3 actions of ItemType C");
        assertEquals(1, repairList.get(0).getCost(), "Oldest action has cost 1");
        assertEquals(2, repairList.get(1).getCost());
        assertEquals(3, repairList.get(2).getCost(), "Newest action has cost 3");

        assertEquals(1, itemActionService.getActionsOnItemTypeSortedByPerformedDate(ItemClass.BALL).size());
        assertEquals(0, itemActionService.getActionsOnItemTypeSortedByPerformedDate(ItemClass.BOX).size());
    }

    @Test
    void getItemIdsWithTotalActionCost() {
        final ItemActionService itemActionService = new ItemActionService();
        itemActionService.registerItem(dogA);
        itemActionService.registerItem(dogB);

        itemActionService.registerActionOnItem(dogA.getId(), "Pet", 100, Instant.now());
        itemActionService.registerActionOnItem(dogB.getId(), "Play", 100, Instant.now());
        itemActionService.registerActionOnItem(dogB.getId(), "Feed", 100, Instant.now());

        HashMap<String, Integer> vehicleRepairCost = new HashMap<>() {{
            put(dogA.getId(), 100);
            put(dogB.getId(), 200);
        }};

        assertEquals(vehicleRepairCost.get(dogA.getId()), itemActionService.getItemIdsWithTotalActionCost().get(dogA.getId()));
        assertEquals(vehicleRepairCost.get(dogB.getId()), itemActionService.getItemIdsWithTotalActionCost().get(dogB.getId()));
    }


    @Test
    void getItemsWithHighestTotalActionCostByItemType() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(boxA);
        itemActionService.registerItem(boxB);

        itemActionService.registerActionOnItem(boxA.getId(), "Lift", 1000, Instant.now());
        itemActionService.registerActionOnItem(boxA.getId(), "Crush", 1000, Instant.now());
        itemActionService.registerActionOnItem(boxB.getId(), "Throw", 1000, Instant.now());

        assertEquals(Collections.singletonMap(boxA, 2000), itemActionService.getItemsWithHighestTotalActionCostByItemType(ItemClass.BOX),
                "Item B1 should the highest action cost.");

        itemActionService.registerItem(dogA);
        itemActionService.registerItem(dogB);
        itemActionService.registerItem(dogC);

        itemActionService.registerActionOnItem(dogA.getId(), "Jump", 100, Instant.now());
        itemActionService.registerActionOnItem(dogB.getId(), "Grow", 100, Instant.now());
        itemActionService.registerActionOnItem(dogC.getId(), "Find", 0, Instant.now());

        HashMap<Item, Integer> pickupARepair = new HashMap<>() {{
            put(dogA, 100);
            put(dogB, 100);
        }};
        assertEquals(pickupARepair, itemActionService.getItemsWithHighestTotalActionCostByItemType(ItemClass.DOG),
                "Both itemC1 and itemC2 have equal and the highest total action cost.");

        assertNull(itemActionService.getItemsWithHighestTotalActionCostByItemType(ItemClass.BALL),
                "No actions for ItemType A should be registered.");
    }

    @Test
    void getItemsPartitionedByClass() {
        final ItemActionService itemActionService = new ItemActionService();

        itemActionService.registerItem(ballA);

        itemActionService.registerItem(boxA);
        itemActionService.registerItem(boxB);
        var partitionedItems = itemActionService.getItemsPartitionedByClass();

        assertEquals(ItemClass.getAllClasses().size(), partitionedItems.size(),
                String.format("We should have %d different Item class objects.", ItemClass.getAllClasses().size()));

        assertEquals(1, partitionedItems.get(Ball.class).size(), "We should have one Ball object.");
        assertEquals(2, partitionedItems.get(Box.class).size(), "We should have to Ball objects.");
        assertEquals(0, partitionedItems.get(Dog.class).size(), "We should have zero Dog objects.");

        itemActionService.registerItem(dogA);
        itemActionService.registerItem(dogB);
        itemActionService.registerItem(dogC);

        partitionedItems = itemActionService.getItemsPartitionedByClass(ItemClass.DOG, ItemClass.BOX);

        assertEquals(2, partitionedItems.size(), "We should have two different Item class objects (Dog and Box).");
        assertTrue(partitionedItems.containsKey(Dog.class));
        assertTrue(partitionedItems.containsKey(Box.class));
        assertFalse(partitionedItems.containsKey(Ball.class));

        assertEquals(3, partitionedItems.get(Dog.class).size(), "We should have three Dog objects.");
    }
}