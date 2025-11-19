package io.sakamotodev.libaries.parrotlib.config;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ParrotConfigHandler {

    private final Plugin plugin;
    private final Map<String, ParrotConfigFile> configs = new HashMap<>();

    public ParrotConfigHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public ParrotConfigFile get(String fileName) {
        return configs.computeIfAbsent(fileName, f -> new ParrotConfigFile(plugin, f));
    }

    public void reloadAll() {
        configs.values().forEach(ParrotConfigFile::load);
    }

    public void saveAll() {
        configs.values().forEach(ParrotConfigFile::save);
    }
}
