package io.sakamotodev.libaries.parrotlib.database.helpers;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public class WriteBehindQueue<V> {

    private final Queue<WriteJob<V>> queue = new ConcurrentLinkedQueue<>();
    private final BiConsumer<UUID, V> writer;

    public WriteBehindQueue(BiConsumer<UUID, V> writer) {
        this.writer = writer;
    }

    public void enqueue(WriteJob<V> job) {
        queue.offer(job);
    }

    public void flush() {
        WriteJob<V> job;
        while ((job = queue.poll()) != null) {
            try {
                writer.accept(job.uuid(), job.data());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
