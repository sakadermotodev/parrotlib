package io.sakamotodev.libaries.parrotlib.item;

import io.sakamotodev.libaries.parrotlib.text.ParrotDecorator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ParrotItem {

    private final ItemStack item;

    public ParrotItem(Material material) {
        this.item = new ItemStack(material);
    }

    public ParrotItem(ItemStack base) {
        this.item = base.clone();
    }

    public ParrotItem amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ParrotItem name(Component name) {
        item.editMeta(meta -> meta.displayName(name));
        return this;
    }

    public ParrotItem name(String... lines) {
        if (lines.length == 0) return this;
        return name(ParrotDecorator.switchStringToComponent(String.join("<newline>", lines)));
    }

    public ParrotItem lore(List<Component> lore) {
        item.editMeta(meta -> meta.lore(lore));
        return this;
    }

    public ParrotItem lore(Component... lines) {
        return lore(List.of(lines));
    }

    public ParrotItem lore(String... lines) {
        return lore(
                List.of(
                        ParrotDecorator.switchStringToComponent(String.join("<newline>", lines))
                ).get(0).children()
        );
    }

    /* -----------------------------
       PDC SUPPORT
    ----------------------------- */

    public ParrotItem pdcString(NamespacedKey key, String value) {
        ParrotItemPDC.setString(item, key, value);
        return this;
    }

    public ParrotItem pdcInt(NamespacedKey key, int value) {
        ParrotItemPDC.setInt(item, key, value);
        return this;
    }

    public ParrotItem pdcBool(NamespacedKey key, boolean value) {
        ParrotItemPDC.setBoolean(item, key, value);
        return this;
    }

    /* -----------------------------
       SKULL SUPPORT
    ----------------------------- */

    public ParrotItem skullPlayer(String name) {
        ParrotItemSkull.applyPlayerName(item, name);
        return this;
    }

    public ParrotItem skullBase64(String base64) {
        ParrotItemSkull.applyBase64(item, base64);
        return this;
    }

    /* -----------------------------
       VISUAL (Glow / Durability / Unbreakable)
    ----------------------------- */

    public ParrotItem durability(int dmg) {
        ParrotItemVisual.setDurability(item, dmg);
        return this;
    }

    public ParrotItem unbreakable(boolean flag) {
        ParrotItemVisual.setUnbreakable(item, flag);
        return this;
    }

    public ParrotItem glow() {
        ParrotItemVisual.glow(item);
        return this;
    }

    /* -----------------------------
       FINISH
    ----------------------------- */

    public ItemStack build() {
        return item;
    }
}
