package io.sakamotodev.libaries.parrotlib.gui;

import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParrotGUIRegistry {
    private static final Map<Inventory, ParrotGUI> active = new ConcurrentHashMap<>();

    public static void register(Inventory inv, ParrotGUI gui) {
        active.put(inv, gui);
    }

    public static ParrotGUI get(Inventory inv) {
        return active.get(inv);
    }

    public static void unregister(Inventory inv) {
        active.remove(inv);
    }
}
