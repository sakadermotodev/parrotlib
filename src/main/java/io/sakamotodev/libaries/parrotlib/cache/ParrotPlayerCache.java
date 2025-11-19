package io.sakamotodev.libaries.parrotlib.cache;

import io.sakamotodev.libaries.parrotlib.database.ParrotDatabase;
import io.sakamotodev.libaries.parrotlib.database.ParrotExecutors;
import io.sakamotodev.libaries.parrotlib.database.helpers.WriteBehindQueue;
import io.sakamotodev.libaries.parrotlib.database.helpers.WriteJob;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ParrotPlayerCache<V> { private final ParrotDatabase db;
    private final Plugin plugin;

    private final Map<UUID, V> cache = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> dirty = new ConcurrentHashMap<>();

    private final Function<UUID, CompletableFuture<V>> loadHandler;
    private BiConsumer<UUID, V> saveHandler;

    private WriteBehindQueue<V> writeQueue;

    private int autosaveTaskId = -1;
    private int autosaveIntervalSeconds = 120;

    public ParrotPlayerCache(Plugin plugin, ParrotDatabase db, Function<UUID, CompletableFuture<V>> loadHandler) {
        this.plugin = plugin;
        this.db = db;
        this.loadHandler = loadHandler;
    }

    public void saveHandler(BiConsumer<UUID, V> saveHandler) {
        this.saveHandler = saveHandler;
        this.writeQueue = new WriteBehindQueue<>(saveHandler);
    }

    public CompletableFuture<V> load(UUID uuid) {
        return loadHandler.apply(uuid).thenApply(v -> {
            cache.put(uuid, v);
            dirty.put(uuid, false);
            return v;
        });
    }

    public void markDirty(UUID uuid) {
        dirty.put(uuid, true);
    }

    public void scheduleWriteBehind(UUID uuid) {
        if (!dirty.getOrDefault(uuid, false)) return;
        V data = cache.get(uuid);
        writeQueue.enqueue(new WriteJob<>(uuid, data));
        dirty.put(uuid, false);
    }

    public void unload(UUID uuid) {
        scheduleWriteBehind(uuid);
        cache.remove(uuid);
        dirty.remove(uuid);
    }

    public void saveAll() {
        for (UUID uuid : cache.keySet()) {
            scheduleWriteBehind(uuid);
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

    public V get(UUID uuid) {
        return cache.get(uuid);
    }

    public boolean has(UUID uuid) {
        return cache.containsKey(uuid);
    }
}
