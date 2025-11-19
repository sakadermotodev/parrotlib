package io.sakamotodev.libaries.parrotlib.config.helper;

import io.sakamotodev.libaries.parrotlib.config.ParrotConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class ParrotConfigMapper {

    private final Plugin plugin;

    public ParrotConfigMapper(Plugin plugin) {
        this.plugin = plugin;
    }

    public <T> T load(Class<T> clazz, String fileName) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            ParrotConfigFile configFile = new ParrotConfigFile(plugin, fileName);
            YamlConfiguration config = configFile.get();

            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(ConfigValue.class)) continue;

                ConfigValue cv = field.getAnnotation(ConfigValue.class);
                String path = cv.value();

                // ensure accessible
                field.setAccessible(true);

                // default value in code:
                Object defaultValue = field.get(instance);

                // if missing in config → write default
                if (!config.contains(path)) {
                    config.set(path, defaultValue);
                }

                // load real value from YAML
                Object value = config.get(path);

                // assign value into field
                field.set(instance, value);
            }

            configFile.save(); // write missing defaults

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Could not load config-mapped class: " + clazz.getName(), e);
        }
    }
}
