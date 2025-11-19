package io.sakamotodev.libaries.parrotlib.gui.helpers;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ParrotGUIFiller {

    private final Inventory inv;
    private final int rows;
    private final int size;

    public ParrotGUIFiller(Inventory inv, int rows) {
        this.inv = inv;
        this.rows = rows;
        this.size = rows * 9;
    }

    public ParrotGUIFiller all(ItemStack item) {
        for (int i = 0; i < size; i++) inv.setItem(i, item);
        return this;
    }

    public ParrotGUIFiller except(ItemStack item, int... excluded) {
        for (int i = 0; i < size; i++) {
            boolean skip = false;
            for (int ex : excluded) if (i == ex) skip = true;
            if (!skip) inv.setItem(i, item);
        }
        return this;
    }

    public ParrotGUIFiller border(ItemStack item) {
        for (int col = 0; col < 9; col++) {
            inv.setItem(col, item);
            inv.setItem(size - 9 + col, item);
        }
        for (int row = 0; row < rows; row++) {
            inv.setItem(row * 9, item);
            inv.setItem(row * 9 + 8, item);
        }
        return this;
    }

    public ParrotGUIFiller border(ItemStack item, int thickness) {
        for (int t = 0; t < thickness; t++) {
            for (int col = t; col < 9 - t; col++) {
                inv.setItem(t * 9 + col, item);
                inv.setItem((rows - 1 - t) * 9 + col, item);
            }
            for (int row = t; row < rows - t; row++) {
                inv.setItem(row * 9 + t, item);
                inv.setItem(row * 9 + (8 - t), item);
            }
        }
        return this;
    }

}
