package io.sakamotodev.libaries.parrotlib.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ParrotItemVisual {

    public static void setDurability(ItemStack item, int durability) {
        item.editMeta(meta -> {
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(durability);
            }
        });
    }

    public static void setUnbreakable(ItemStack item, boolean value) {
        item.editMeta(meta -> meta.setUnbreakable(value));
    }

    public static void glow(ItemStack item) {
        item.editMeta(meta -> {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
    }

}
