package io.sakamotodev.libaries.parrotlib.item;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.util.UUID;

public class ParrotItemSkull {
    public static void applyPlayerName(ItemStack item, String name) {
        item.editMeta(SkullMeta.class, meta ->
                meta.setPlayerProfile(Bukkit.createProfile(name)));
    }

    public static void applyBase64(ItemStack item, String base64) {
        item.editMeta(SkullMeta.class, meta -> {
            try {
                Class<?> profileClass = Class.forName("com.mojang.authlib.GameProfile");
                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");

                Object profile = profileClass
                        .getConstructor(UUID.class, String.class)
                        .newInstance(UUID.randomUUID(), null);

                Object properties = profileClass.getMethod("getProperties").invoke(profile);

                properties.getClass()
                        .getMethod("put", Object.class, Object.class)
                        .invoke(properties,
                                "textures",
                                propertyClass.getConstructor(String.class, String.class)
                                        .newInstance("textures", base64));

                var field = meta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(meta, profile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
