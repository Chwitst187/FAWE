package com.fastasyncworldedit.bukkit.util;

import com.fastasyncworldedit.core.util.TaskManager;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * TaskManager implementation for Folia's region-threaded scheduler.
 * Folia removed the global Bukkit scheduler and replaced it with region-based scheduling.
 */
public class FoliaTaskManager extends TaskManager {

    private final Plugin plugin;
    private final GlobalRegionScheduler scheduler;

    public FoliaTaskManager(final Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = Bukkit.getGlobalRegionScheduler();
    }

    @Override
    public int repeat(@Nonnull final Runnable runnable, final int interval) {
        // Folia doesn't return task IDs in the same way, so we return -1
        // The task reference is kept internally but we can't return a meaningful ID
        // Ensure interval is at least 1 tick
        int safeInterval = Math.max(1, interval);
        scheduler.runAtFixedRate(plugin, task -> runnable.run(), 1, safeInterval);
        return -1;
    }

    @Override
    public int repeatAsync(@Nonnull final Runnable runnable, final int interval) {
        // For async tasks, we use the async scheduler
        // Ensure interval is at least 1 tick (50ms)
        long safeInterval = Math.max(50L, 50L * interval);
        Bukkit.getAsyncScheduler().runAtFixedRate(
                plugin,
                task -> runnable.run(),
                safeInterval,
                safeInterval,
                TimeUnit.MILLISECONDS
        );
        return -1;
    }

    @Override
    public void async(@Nonnull final Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    @Override
    public void task(@Nonnull final Runnable runnable) {
        scheduler.run(plugin, task -> runnable.run());
    }

    @Override
    public void later(@Nonnull final Runnable runnable, final int delay) {
        // Folia requires delay > 0, so if delay is 0 or negative, run immediately
        if (delay <= 0) {
            scheduler.run(plugin, task -> runnable.run());
        } else {
            scheduler.runDelayed(plugin, task -> runnable.run(), delay);
        }
    }

    @Override
    public void laterAsync(@Nonnull final Runnable runnable, final int delay) {
        // Folia requires delay > 0, so if delay is 0 or negative, run immediately
        if (delay <= 0) {
            Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
        } else {
            Bukkit.getAsyncScheduler().runDelayed(
                    plugin,
                    task -> runnable.run(),
                    50L * delay,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    @Override
    public void cancel(final int task) {
        // Folia doesn't support canceling tasks by ID in the traditional way
        // Tasks must be canceled via their ScheduledTask reference
        // Since we don't track task references here, we can't cancel by ID
        // This is a limitation of the Folia scheduler model
    }

}

