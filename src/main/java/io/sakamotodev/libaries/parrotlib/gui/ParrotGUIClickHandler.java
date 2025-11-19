package io.sakamotodev.libaries.parrotlib.gui;

import io.sakamotodev.libaries.parrotlib.gui.helpers.ParrotGUISettings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ParrotGUIClickHandler {

    private final Inventory inventory;
    private final Map<Integer, ParrotGUIClick> handlers = new HashMap<>();
    private final Map<Player, Long> lastClick = new WeakHashMap<>();
    private final Map<Player, Map<Integer, Long>> lastSlotClick = new WeakHashMap<>();

    public ParrotGUIClickHandler(Inventory inv) {
        this.inventory = inv;
    }

    public ParrotGUIClickHandler set(int slot, ItemStack item, ParrotGUIClick handler) {
        handlers.put(slot, handler);
        inventory.setItem(slot, item);
        return this;
    }

    public ParrotGUIClickHandler set(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    public void click(Player player, int slot, ParrotGUISettings settings) {

        long now = System.currentTimeMillis();

        long globalCd = settings.clickCooldown();
        if (globalCd > 0) {
            long last = lastClick.getOrDefault(player, 0L);
            if (now - last < globalCd) {
                return;
            }
            lastClick.put(player, now);
        }

        long slotCd = settings.slotCooldown(slot);
        if (slotCd > 0) {
            lastSlotClick.putIfAbsent(player, new HashMap<>());
            long last = lastSlotClick.get(player).getOrDefault(slot, 0L);

            if (now - last < slotCd) {
                return;
            }
            lastSlotClick.get(player).put(slot, now);
        }

        if (handlers.containsKey(slot)) {
            handlers.get(slot).onClick(player, slot);
        }
    }

}
