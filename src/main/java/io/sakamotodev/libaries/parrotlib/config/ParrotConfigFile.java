package io.sakamotodev.libaries.parrotlib.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ParrotConfigFile {


    private final Plugin plugin;
    private final String fileName;
    private final File file;
    private YamlConfiguration config;

    public ParrotConfigFile(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.file = new File(plugin.getDataFolder(), this.fileName);

        createIfMissing();
        load();
    }

    private void createIfMissing() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(fileName, false);
        }
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration get() {
        return config;
    }

    public File getFile() {
        return file;
    }
}
