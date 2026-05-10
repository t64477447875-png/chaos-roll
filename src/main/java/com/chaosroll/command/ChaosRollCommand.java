package com.chaosroll.command;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.event.ActiveEffectsManager;
import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventRegistry;
import com.chaosroll.network.NetworkHandler;
import com.chaosroll.timer.RollTimerManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public final class ChaosRollCommand {

    private ChaosRollCommand() {}

    private static final SuggestionProvider<CommandSourceStack> EVENT_ID_SUGGESTIONS =
            (ctx, builder) -> {
                String remaining = builder.getRemaining().toLowerCase();
                for (BaseEvent e : EventRegistry.all()) {
                    if (e.getId().toLowerCase().startsWith(remaining)) {
                        builder.suggest(e.getId());
                    }
                }
                return builder.buildFuture();
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("chaosroll")
                .then(Commands.literal("config")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("reload").executes(ChaosRollCommand::reloadConfig))
                        .then(Commands.literal("show").executes(ChaosRollCommand::showConfig)))
                .then(Commands.literal("reload")
                        .requires(src -> src.hasPermission(3))
                        .executes(ChaosRollCommand::reloadConfig))
                .then(Commands.literal("roll")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ChaosRollCommand::rollPlayer)))
                .then(Commands.literal("event")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests(EVENT_ID_SUGGESTIONS)
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ChaosRollCommand::runEventForPlayer))))
                .then(Commands.literal("pause")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("all").executes(ChaosRollCommand::pauseAll))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ChaosRollCommand::pausePlayer)))
                .then(Commands.literal("list").executes(ChaosRollCommand::listEvents))
                .then(Commands.literal("status")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ChaosRollCommand::showStatus)))
                .then(Commands.literal("stats")
                        .executes(ctx -> showStats(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> showStats(ctx, EntityArgument.getPlayer(ctx, "player")))))
                .then(Commands.literal("vote")
                        .then(Commands.literal("yes").executes(ctx -> voteCast(ctx, true)))
                        .then(Commands.literal("no").executes(ctx -> voteCast(ctx, false)))
                        .then(Commands.literal("status").executes(ChaosRollCommand::voteStatus)))
                .then(Commands.literal("achievements")
                        .executes(ChaosRollCommand::listAchievements))
                .then(Commands.literal("block")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests(EVENT_ID_SUGGESTIONS)
                                .executes(ChaosRollCommand::toggleBlock)))
                .then(Commands.literal("blocklist")
                        .executes(ChaosRollCommand::showBlocklist));
        dispatcher.register(root);
    }

    private static int voteCast(CommandContext<CommandSourceStack> ctx, boolean yes) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (!com.chaosroll.vote.VoteManager.hasActiveVote()) {
            error(ctx, "Зараз немає активного голосування.");
            return 0;
        }
        com.chaosroll.vote.VoteManager.castVote(p, yes);
        success(ctx, "Голос зараховано: " + (yes ? "ПРОПУСТИТИ" : "ЗАЛИШИТИ") + ".");
        return 1;
    }

    private static int voteStatus(CommandContext<CommandSourceStack> ctx) {
        if (!com.chaosroll.vote.VoteManager.hasActiveVote()) {
            info(ctx, "Активного голосування немає.");
            return 1;
        }
        info(ctx, "Голосування: " + com.chaosroll.vote.VoteManager.currentEventName()
                + " — §aYES: " + com.chaosroll.vote.VoteManager.getYesCount()
                + "§r, §cNO: " + com.chaosroll.vote.VoteManager.getNoCount());
        return 1;
    }

    private static int listAchievements(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (p.getServer() == null) return 0;
        com.chaosroll.data.ChaosRollSavedData data = com.chaosroll.data.ChaosRollSavedData.get(p.getServer());
        com.chaosroll.data.PlayerStats st = data.getOrCreateStats(p.getUUID());
        info(ctx, "Досягнення (" + st.achievements.size() + "/"
                + com.chaosroll.achievement.AchievementRegistry.ALL.size() + "):");
        for (com.chaosroll.achievement.Achievement a : com.chaosroll.achievement.AchievementRegistry.ALL.values()) {
            String mark = st.achievements.contains(a.id) ? "§a✔" : "§7✘";
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "  " + mark + " §f" + a.displayName + " §7— " + a.description), false);
        }
        return 1;
    }

    private static int toggleBlock(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (p.getServer() == null) return 0;
        String id = StringArgumentType.getString(ctx, "id");
        com.chaosroll.event.BaseEvent ev = EventRegistry.getById(id);
        if (ev == null) {
            error(ctx, "Подія '" + id + "' не знайдена.");
            return 0;
        }
        com.chaosroll.data.ChaosRollSavedData data = com.chaosroll.data.ChaosRollSavedData.get(p.getServer());
        com.chaosroll.data.PlayerStats st = data.getOrCreateStats(p.getUUID());
        if (st.blocklist.contains(id)) {
            st.blocklist.remove(id);
            success(ctx, "Подію '" + ev.getDisplayName() + "' розблоковано.");
        } else {
            st.blocklist.add(id);
            success(ctx, "Подію '" + ev.getDisplayName() + "' заблоковано (більше не випаде тобі).");
        }
        data.setDirty();
        return 1;
    }

    private static int showBlocklist(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (p.getServer() == null) return 0;
        com.chaosroll.data.ChaosRollSavedData data = com.chaosroll.data.ChaosRollSavedData.get(p.getServer());
        com.chaosroll.data.PlayerStats st = data.getOrCreateStats(p.getUUID());
        if (st.blocklist.isEmpty()) {
            info(ctx, "Твій блок-лист порожній.");
            return 1;
        }
        info(ctx, "Заблоковано (" + st.blocklist.size() + "):");
        for (String id : st.blocklist) {
            com.chaosroll.event.BaseEvent ev = EventRegistry.getById(id);
            String name = ev == null ? id : ev.getDisplayName();
            ctx.getSource().sendSuccess(() -> Component.literal("  §c✘ §f" + name + " §7(" + id + ")"), false);
        }
        return 1;
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> ctx) {
        ConfigManager.reload();
        success(ctx, "Config reloaded.");
        return 1;
    }

    private static int showConfig(CommandContext<CommandSourceStack> ctx) {
        var c = ConfigManager.get();
        info(ctx, "interval=" + c.rollIntervalSeconds + "s"
                + ", balance=" + c.balanceMode
                + ", preventDeath=" + c.preventDirectDeath
                + ", global=" + c.globalEventsEnabled
                + ", maxActive=" + c.maxActiveEffects);
        return 1;
    }

    private static int rollPlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        boolean ok = NetworkHandler.forceRoll(target, true);
        if (!ok) {
            error(ctx, "No events available to roll.");
            return 0;
        }
        success(ctx, "Forced roll for " + target.getName().getString() + ".");
        return 1;
    }

    private static int runEventForPlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        BaseEvent event = EventRegistry.getById(id);
        if (event == null) {
            error(ctx, "Unknown event id: " + id);
            return 0;
        }
        boolean withAnimation = ConfigManager.get().enableRollAnimation;
        boolean ok = NetworkHandler.forceEvent(target, event, withAnimation);
        if (!ok) {
            error(ctx, "Failed to run event.");
            return 0;
        }
        success(ctx, "Ran '" + event.getDisplayName() + "' for " + target.getName().getString() + ".");
        return 1;
    }

    private static int pauseAll(CommandContext<CommandSourceStack> ctx) {
        boolean nowPaused = RollTimerManager.togglePauseAll();
        success(ctx, nowPaused ? "Timer paused for ALL players." : "Timer resumed for ALL players.");
        return 1;
    }

    private static int pausePlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        boolean nowPaused = RollTimerManager.togglePause(target);
        success(ctx, (nowPaused ? "Paused " : "Resumed ") + "timer for " + target.getName().getString() + ".");
        return 1;
    }

    private static int listEvents(CommandContext<CommandSourceStack> ctx) {
        List<BaseEvent> all = EventRegistry.all();
        info(ctx, "Registered events (" + all.size() + "):");
        for (BaseEvent e : all) {
            ChatFormatting color = switch (e.getType()) {
                case POSITIVE -> ChatFormatting.GREEN;
                case NEGATIVE -> ChatFormatting.RED;
                case CHAOTIC  -> ChatFormatting.GOLD;
            };
            String typeLabel = e.getType().name().substring(0, 3);
            String line = "  " + e.getId() + "  [" + typeLabel + "]  " + e.getDisplayName();
            ctx.getSource().sendSuccess(() -> Component.literal(line).withStyle(color), false);
        }
        return 1;
    }

    private static int showStatus(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        UUID id = target.getUUID();
        int seconds = RollTimerManager.getSecondsRemaining(id);
        boolean paused = RollTimerManager.isPaused(id);
        boolean ready = RollTimerManager.isRollReady(target);
        int streak = EventRegistry.getNegativeStreak(id, target.getServer());
        int posStreak = EventRegistry.getPositiveStreak(id, target.getServer());
        List<String> active = ActiveEffectsManager.getActiveDisplayNames(id);

        info(ctx, "Status for " + target.getName().getString() + ":");
        info(ctx, "  Cooldown: " + (ready ? "READY" : seconds + "s remaining")
                + (paused ? " (PAUSED)" : ""));
        info(ctx, "  Negative streak: " + streak + ", Positive streak: " + posStreak);
        if (active.isEmpty()) {
            info(ctx, "  Active effects: none");
        } else {
            info(ctx, "  Active effects: " + String.join(", ", active));
        }
        return 1;
    }

    private static int showStats(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        if (target == null || target.getServer() == null) {
            error(ctx, "Player not found.");
            return 0;
        }
        com.chaosroll.data.ChaosRollSavedData data =
                com.chaosroll.data.ChaosRollSavedData.get(target.getServer());
        com.chaosroll.data.PlayerStats s = data.stats.get(target.getUUID());
        if (s == null || s.totalRolls == 0) {
            info(ctx, "No stats yet for " + target.getName().getString() + ".");
            return 1;
        }
        info(ctx, "Stats for " + target.getName().getString() + ":");
        info(ctx, "  Total rolls: " + s.totalRolls);
        int total = Math.max(1, s.totalRolls);
        info(ctx, "  Positive: " + s.positiveCount + " (" + (s.positiveCount * 100 / total) + "%)");
        info(ctx, "  Negative: " + s.negativeCount + " (" + (s.negativeCount * 100 / total) + "%)");
        info(ctx, "  Chaotic:  " + s.chaoticCount + " (" + (s.chaoticCount * 100 / total) + "%)");
        info(ctx, "  Top events:");
        s.byEventId.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(e -> info(ctx, "    " + e.getKey() + " - " + e.getValue() + "x"));
        return 1;
    }

    private static void success(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> Component.literal("[Chaos Roll] " + msg)
                .withStyle(ChatFormatting.GREEN), true);
    }

    private static void error(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendFailure(Component.literal("[Chaos Roll] " + msg)
                .withStyle(ChatFormatting.RED));
    }

    private static void info(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> Component.literal("[Chaos Roll] " + msg)
                .withStyle(ChatFormatting.GRAY), false);
    }
}
