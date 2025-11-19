package io.sakamotodev.libaries.parrotlib.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ParrotExecutors {

    private static ExecutorService databasePool;

    public static void init(int threads) {
        if (databasePool != null && !databasePool.isShutdown()) return;

        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setName("ParrotLib-DB-" + t.getId());
            t.setDaemon(true);
            return t;
        };

        databasePool = Executors.newFixedThreadPool(threads, factory);
    }

    public static ExecutorService db() {
        return databasePool;
    }

    public static void shutdown() {
        if (databasePool != null && !databasePool.isShutdown()) {
            databasePool.shutdownNow();
        }
    }
}
