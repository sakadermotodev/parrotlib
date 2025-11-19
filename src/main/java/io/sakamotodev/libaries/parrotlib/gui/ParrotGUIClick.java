package io.sakamotodev.libaries.parrotlib.gui;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ParrotGUIClick {
    void onClick(Player player, int slot);
}
