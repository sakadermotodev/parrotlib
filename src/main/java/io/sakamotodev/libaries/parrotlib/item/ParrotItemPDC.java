package io.sakamotodev.libaries.parrotlib.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ParrotItemPDC {

    public static void setString(ItemStack item, NamespacedKey key, String value) {
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(key, PersistentDataType.STRING, value));
    }

    public static String getString(ItemStack item, NamespacedKey key) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.get(key, PersistentDataType.STRING);
    }

    public static void setInt(ItemStack item, NamespacedKey key, int value) {
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(key, PersistentDataType.INTEGER, value));
    }

    public static Integer getInt(ItemStack item, NamespacedKey key) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.get(key, PersistentDataType.INTEGER);
    }

    public static void setBoolean(ItemStack item, NamespacedKey key, boolean value) {
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(key, PersistentDataType.INTEGER, value ? 1 : 0));
    }

    public static Boolean getBoolean(ItemStack item, NamespacedKey key) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        Integer v = pdc.get(key, PersistentDataType.INTEGER);
        return v != null && v == 1;
    }

    public static boolean has(ItemStack item, NamespacedKey key) {
        return item.getItemMeta().getPersistentDataContainer().has(key);
    }

}
