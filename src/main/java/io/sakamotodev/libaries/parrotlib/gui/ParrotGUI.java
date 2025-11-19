package io.sakamotodev.libaries.parrotlib.gui;

import io.sakamotodev.libaries.parrotlib.gui.helpers.ParrotGUIFiller;
import io.sakamotodev.libaries.parrotlib.gui.helpers.ParrotGUISettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

public class ParrotGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Plugin plugin;
    private final ParrotGUISettings settings;
    private final ParrotGUIClickHandler clickHandler;
    private final ParrotGUIFiller filler;

    public ParrotGUI(Plugin plugin, String title, int rows) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.settings = new ParrotGUISettings(rows);
        this.clickHandler = new ParrotGUIClickHandler(inventory);
        this.filler = new ParrotGUIFiller(inventory, rows);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        ParrotGUIRegistry.register(inventory, this);
        player.openInventory(inventory);
    }

    public void click(Player player, int slot) {
        clickHandler.click(player, slot, settings);
    }

    public ParrotGUIClickHandler items() {
        return clickHandler;
    }

    public ParrotGUISettings settings() {
        return settings;
    }

    public ParrotGUIFiller fill() {
        return filler;
    }

    public Plugin plugin() {
        return plugin;
    }
}
