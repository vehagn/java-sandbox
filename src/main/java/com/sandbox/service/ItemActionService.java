package com.sandbox.service;

import com.sandbox.exceptions.ActionNotFoundException;
import com.sandbox.exceptions.ItemIdAlreadyRegisteredException;
import com.sandbox.exceptions.ItemNotFoundException;
import com.sandbox.model.Action;
import com.sandbox.model.Item;
import com.sandbox.model.constants.ItemType;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ItemActionService {
    private final Map<String, Item> items = new HashMap<>();
    private final Set<Action> actions = new HashSet<>();

    public ItemActionService() {
        // empty
    }

    public Item getItemById(final String itemId) {
        final Item item = items.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException();
        }
        return item;
    }

    public Action getActionById(final UUID actionId) {
        final Action action = actions.stream()
                .filter(a -> a.getId().equals(actionId))
                .findFirst().orElse(null);
        if (action == null) {
            throw new ActionNotFoundException();
        }
        return action;
    }

    public void registerItem(final Item item) {
        if (items.containsKey(item.getId())) {
            throw new ItemIdAlreadyRegisteredException();
        }
        items.put(item.getId(), item);
    }

    public void registerItem(final String itemId, final ItemType itemType, final String name) {
        Item item = new Item(itemId, itemType, name);
        this.registerItem(item);
    }

    public UUID registerActionOnItem(final String itemId, final String actionDescription, final Integer actionCost, final Instant actionDate) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException();
        }

        Action action;
        // On the off chance that the created UUID already exists, create a new Action with a different UUID.
        do action = new Action(itemId, actionDescription, actionCost, actionDate);
        while (!actions.add(action));

        return action.getId();
    }

    public Set<Item> getAllItems() {
        return items.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Set<Action> getAllActions() {
        return actions.stream().collect(Collectors.toUnmodifiableSet());
    }

    public Map<Action, Item> getActionOnItemByActionId(final UUID uuid) {
        final Action action = this.getActionById(uuid);
        final Item item = items.get(action.getItemId());

        return Collections.singletonMap(action, item);
    }

    public Set<String> getItemIdsOfType(ItemType itemType) {
        return items.values().stream()
                .filter(item -> item.getType().equals(itemType))
                .map(Item::getId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Action> getActionsOnItemTypeSortedByPerformedDate(ItemType itemType) {
        final Set<String> itemIds = this.getItemIdsOfType(itemType);

        return actions.stream()
                .filter(action -> itemIds.contains(action.getItemId()))
                .sorted(Comparator.comparing(Action::getPerformedDateTime))
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<String, Integer> getItemIdsWithTotalActionCost() {
        final Map<String, Integer> itemActionCost = new HashMap<>();

        actions.forEach(action -> {
            itemActionCost.putIfAbsent(action.getItemId(), 0);
            itemActionCost.computeIfPresent(action.getItemId(), (k, val) -> val + action.getCost());
        });

        return itemActionCost;
    }

    public Map<String, Integer> getItemIdsWithTotalActionCostByItemType(ItemType itemType) {
        final Set<String> itemIdsOfType = this.getItemIdsOfType(itemType);

        return this.getItemIdsWithTotalActionCost().entrySet()
                .stream().filter(e -> itemIdsOfType.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Finds the {@code Item} of type {@code itemType} with the highest total action cost.
     * If more than one {@code Item} have the same highest total cost all are returned.
     *
     * @param itemType {@code ItemType}
     * @return {@code Item}(s) of type {@code itemType} with the highest total cost together with the highest cost
     */
    public Map<Item, Integer> getItemsWithHighestTotalActionCostByItemType(ItemType itemType) {
        final Map<String, Integer> itemIdsTotalActionCostMap = this.getItemIdsWithTotalActionCostByItemType(itemType);

        final Optional<Map.Entry<String, Integer>> maxEntry = itemIdsTotalActionCostMap.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

        return maxEntry
                .map(entry -> itemIdsTotalActionCostMap.entrySet().stream()
                        .filter(e -> e.getValue().equals(entry.getValue()))
                        .collect(Collectors.toMap(
                                e -> items.get(e.getKey()),
                                Map.Entry::getValue)))
                .orElse(null);
    }
}