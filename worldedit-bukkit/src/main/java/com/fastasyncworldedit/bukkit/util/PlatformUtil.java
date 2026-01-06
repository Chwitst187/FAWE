package com.fastasyncworldedit.bukkit.util;

import org.bukkit.Bukkit;

/**
 * Utility class to detect if the server is running on Folia.
 */
public class PlatformUtil {

    private static Boolean isFolia = null;

    /**
     * Check if the server is running on Folia.
     * Folia is a Paper fork that implements regionized multithreading.
     *
     * @return true if running on Folia, false otherwise
     */
    public static boolean isFolia() {
        if (isFolia == null) {
            try {
                // Try to access a Folia-specific class
                Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
                // Verify we can actually get the scheduler
                Bukkit.getGlobalRegionScheduler();
                isFolia = true;
            } catch (ClassNotFoundException | NoSuchMethodError | NoClassDefFoundError e) {
                isFolia = false;
            }
        }
        return isFolia;
    }

}

