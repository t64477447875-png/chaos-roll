package com.chaosroll.event;

import com.chaosroll.ChaosRollMod;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class ScheduledTaskManager {

    private static final List<Task> TASKS = new ArrayList<>();
    private static final List<Task> PENDING_ADD = new ArrayList<>();
    private static boolean iterating = false;

    private ScheduledTaskManager() {}

    public static void schedule(MinecraftServer server, int delayTicks, Consumer<MinecraftServer> action) {
        Task task = new Task(server.getTickCount() + delayTicks, action);
        if (iterating) {
            PENDING_ADD.add(task);
        } else {
            TASKS.add(task);
        }
    }

    public static void tick(MinecraftServer server) {
        int now = server.getTickCount();
        iterating = true;
        try {
            Iterator<Task> it = TASKS.iterator();
            while (it.hasNext()) {
                Task t = it.next();
                if (t.triggerTick <= now) {
                    try {
                        t.action.accept(server);
                    } catch (Throwable err) {
                        ChaosRollMod.LOGGER.warn("[Chaos Roll] Scheduled task failed: {}", err.getMessage());
                    }
                    it.remove();
                }
            }
        } finally {
            iterating = false;
        }
        if (!PENDING_ADD.isEmpty()) {
            TASKS.addAll(PENDING_ADD);
            PENDING_ADD.clear();
        }
    }

    private record Task(int triggerTick, Consumer<MinecraftServer> action) {}
}
