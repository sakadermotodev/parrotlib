package io.sakamotodev.libaries.parrotlib.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class ParrotDecorator {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private ParrotDecorator() {}

    public static Component switchStringToComponent(String string) {
        return mm.deserialize(string);
    }

    public static void message(Player player, String message) {
        player.sendMessage(switchStringToComponent(message));
    }

    public static void message(Player player, Component component) {
        player.sendMessage(component);
    }

    public static void actionbar(Player player, String message) {
        player.sendActionBar(switchStringToComponent(message));
    }

    public static void actionbar(Player player, Component component) {
        player.sendActionBar(component);
    }

    public static void title(Player player, String title, String subtitle) {
        player.showTitle(Title.title(
                switchStringToComponent(title),
                switchStringToComponent(subtitle)
        ));
    }

    public static void title(Player player, String title) {
        player.showTitle(Title.title(
                switchStringToComponent(title),
                Component.empty()
        ));
    }

    public static void titleWithDuration(Player player, String title, String subtitle,
                                         int fadeInTicks, int stayTicks, int fadeOutTicks) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeInTicks * 50L),
                Duration.ofMillis(stayTicks * 50L),
                Duration.ofMillis(fadeOutTicks * 50L)
        );

        player.showTitle(Title.title(
                switchStringToComponent(title),
                switchStringToComponent(subtitle),
                times
        ));
    }
}
