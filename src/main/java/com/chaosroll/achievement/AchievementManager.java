package com.chaosroll.achievement;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.data.ChaosRollSavedData;
import com.chaosroll.data.PlayerStats;
import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class AchievementManager {

    private AchievementManager() {}

    public static void recordRoll(ServerPlayer player, BaseEvent event) {
        if (player.getServer() == null) return;
        if (!ConfigManager.get().enableAchievements) return;
        ChaosRollSavedData data = ChaosRollSavedData.get(player.getServer());
        PlayerStats st = data.getOrCreateStats(player.getUUID());

        if (event.getType() == EventType.POSITIVE) {
            st.consecutivePositive++;
            st.consecutiveNegative = 0;
            if (st.consecutivePositive > st.positiveStreakBest) st.positiveStreakBest = st.consecutivePositive;
        } else if (event.getType() == EventType.NEGATIVE) {
            st.consecutiveNegative++;
            st.consecutivePositive = 0;
            if (st.consecutiveNegative > st.negativeStreakBest) st.negativeStreakBest = st.consecutiveNegative;
        } else {
            st.consecutivePositive = 0;
            st.consecutiveNegative = 0;
        }

        if ("mob_morph".equals(event.getId())) st.morphCount++;
        if ("tornado".equals(event.getId())) st.tornadoSurvived++;
        if ("screen_flip".equals(event.getId())) st.screenFlippedCount++;

        check(player, st, "first_roll", st.totalRolls);
        check(player, st, "roller_10", st.totalRolls);
        check(player, st, "roller_100", st.totalRolls);
        check(player, st, "roller_500", st.totalRolls);
        check(player, st, "positive_25", st.positiveCount);
        check(player, st, "negative_25", st.negativeCount);
        check(player, st, "chaotic_25", st.chaoticCount);
        check(player, st, "positive_streak_5", st.consecutivePositive);
        check(player, st, "negative_streak_5", st.consecutiveNegative);
        check(player, st, "tornado_survived", st.tornadoSurvived);
        check(player, st, "morph_5", st.morphCount);
        check(player, st, "screen_flipped", st.screenFlippedCount);
        check(player, st, "guardian_used", st.guardianUsed);
        check(player, st, "vote_skipped_5", st.voteSkippedCount);

        data.setDirty();
    }

    public static void recordGuardianUsed(ServerPlayer player) {
        if (player.getServer() == null) return;
        ChaosRollSavedData data = ChaosRollSavedData.get(player.getServer());
        PlayerStats st = data.getOrCreateStats(player.getUUID());
        st.guardianUsed++;
        check(player, st, "guardian_used", st.guardianUsed);
        data.setDirty();
    }

    public static void recordVoteSkipped(MinecraftServer server) {
        if (server == null) return;
        ChaosRollSavedData data = ChaosRollSavedData.get(server);
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            PlayerStats st = data.getOrCreateStats(p.getUUID());
            st.voteSkippedCount++;
            check(p, st, "vote_skipped_5", st.voteSkippedCount);
        }
        data.setDirty();
    }

    private static void check(ServerPlayer player, PlayerStats st, String id, int value) {
        if (st.achievements.contains(id)) return;
        Achievement a = AchievementRegistry.get(id);
        if (a == null) return;
        if (value >= a.targetCount) {
            st.achievements.add(id);
            unlocked(player, a);
        }
    }

    private static void unlocked(ServerPlayer player, Achievement a) {
        player.sendSystemMessage(Component.literal(
                "§6§l[Досягнення] §r§a" + a.displayName + " §7— " + a.description));
        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    /** Build a tab-list footer with progress summary. */
    public static Component buildTabFooter(ServerPlayer player) {
        if (player.getServer() == null) return Component.empty();
        if (!ConfigManager.get().enableAchievements) return Component.empty();
        ChaosRollSavedData data = ChaosRollSavedData.get(player.getServer());
        PlayerStats st = data.getOrCreateStats(player.getUUID());
        int total = AchievementRegistry.ALL.size();
        int unlocked = st.achievements.size();
        StringBuilder sb = new StringBuilder();
        sb.append("\n§6Chaos Roll §f");
        sb.append("§a").append(unlocked).append("§7/§f").append(total).append(" §7досягнень");
        sb.append("  §7| §fРолів: §a").append(st.totalRolls);
        sb.append("  §7| §a").append(st.positiveCount);
        sb.append(" §c").append(st.negativeCount);
        sb.append(" §e").append(st.chaoticCount);
        sb.append("\n§7Найкращий стрік: §a+").append(st.positiveStreakBest);
        sb.append(" §c-").append(st.negativeStreakBest);
        return Component.literal(sb.toString());
    }
}
