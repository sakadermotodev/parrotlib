package io.sakamotodev.libaries.parrotlib.cache;

import io.sakamotodev.libaries.parrotlib.database.ParrotDatabase;
import io.sakamotodev.libaries.parrotlib.database.ParrotExecutors;
import io.sakamotodev.libaries.parrotlib.database.helpers.WriteBehindQueue;
import io.sakamotodev.libaries.parrotlib.database.helpers.WriteJob;
import io.sakamotodev.libaries.parrotlib.orm.ParrotORM;
import io.sakamotodev.libaries.parrotlib.orm.mappings.TableMeta;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ParrotEntityCache<T> {

    private final Plugin plugin;
    private final ParrotDatabase db;
    private final ParrotORM orm;
    private final Class<T> clazz;
    private final TableMeta meta;

    private final Map<UUID, T> cache = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> dirty = new ConcurrentHashMap<>();

    private final WriteBehindQueue<T> writeQueue;

    private int autosaveTaskId = -1;
    private int autosaveIntervalSeconds = 120;

    public ParrotEntityCache(Plugin plugin, ParrotDatabase db, ParrotORM orm, Class<T> clazz) {
        this.plugin = plugin;
        this.db = db;
        this.orm = orm;
        this.clazz = clazz;

        this.meta = orm.analyze(clazz);
        orm.createTable(clazz);

        this.writeQueue = new WriteBehindQueue<>((uuid, entity) -> orm.save(entity));
    }

    public CompletableFuture<T> load(UUID uuid) {
        return orm.load(clazz, uuid).thenApply(entity -> {
            if (entity == null) {
                try {
                    entity = clazz.getDeclaredConstructor().newInstance();
                    meta.idField.field.set(entity, uuid);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            cache.put(uuid, entity);
            dirty.put(uuid, false);
            return entity;
        });
    }

    public void markDirty(UUID uuid) {
        dirty.put(uuid, true);
    }

    private void writeBehind(UUID uuid) {
        if (!dirty.getOrDefault(uuid, false)) return;

        T entity = cache.get(uuid);
        if (entity == null) return;

        writeQueue.enqueue(new WriteJob<>(uuid, entity));
        dirty.put(uuid, false);
    }

    public void unload(UUID uuid) {
        writeBehind(uuid);
        cache.remove(uuid);
        dirty.remove(uuid);
    }

    public T get(UUID uuid) {
        return cache.get(uuid);
    }

    public boolean isLoaded(UUID uuid) {
        return cache.containsKey(uuid);
    }

    public void saveAll() {
        for (UUID uuid : cache.keySet()) {
            writeBehind(uuid);
        }

        ParrotExecutors.db().submit(writeQueue::flush);
    }

    public void startAutoSave() {
        if (autosaveTaskId != -1) return;

        autosaveTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                () -> ParrotExecutors.db().submit(this::saveAll),
                autosaveIntervalSeconds * 20L,
                autosaveIntervalSeconds * 20L
        );
    }

    public void stopAutoSave() {
        if (autosaveTaskId != -1) {
            Bukkit.getScheduler().cancelTask(autosaveTaskId);
            autosaveTaskId = -1;
        }
        ParrotExecutors.db().submit(writeQueue::flush);
    }

    public void setAutoSaveInterval(int seconds) {
        this.autosaveIntervalSeconds = seconds;
        if (autosaveTaskId != -1) {
            stopAutoSave();
            startAutoSave();
        }
    }
}
