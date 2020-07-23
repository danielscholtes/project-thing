package me.scholtes.proceduraldungeons;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class AsyncScheduler {

    private static final int MAX_CAPACITY = 1024;

    private static Logger logger = Logger.getLogger(AsyncScheduler.class.getName());

    private static ExecutorService executorService;

    public AsyncScheduler() {
        executorService = new ThreadPoolExecutor(6, 32, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_CAPACITY),
                new ThreadFactoryBuilder()
                        .setNameFormat("AthenaHelper Pool Thread #%1$d")
                        .setDaemon(true)
                        .build()
        );
    }

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }

    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, executorService)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    public static void shutdown() {
        MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.MINUTES);
    }


}