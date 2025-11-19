package io.sakamotodev.libaries.parrotlib.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ParrotGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ParrotGUI gui = ParrotGUIRegistry.get(event.getInventory());
        if (gui == null) return;

        event.setCancelled(true);
        if (event.isShiftClick() && gui.settings().shiftClickDisabled()) {
            return;
        }

        gui.items().click(player, event.getRawSlot(), gui.settings());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        ParrotGUI gui = ParrotGUIRegistry.get(event.getInventory());
        if (gui == null) return;

        if (gui.settings().preventClose()) {
            Bukkit.getScheduler().runTask(gui.plugin(), () ->
                    event.getPlayer().openInventory(gui.getInventory())
            );
            return;
        }

        ParrotGUIRegistry.unregister(event.getInventory());
    }
}
