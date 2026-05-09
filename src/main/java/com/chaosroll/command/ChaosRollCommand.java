package com.chaosroll.command;

import com.chaosroll.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ChaosRollCommand {

    private ChaosRollCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("chaosroll")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload").executes(ctx -> {
                            ConfigManager.reload();
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "[Chaos Roll] Config reloaded.")
                                    .withStyle(ChatFormatting.GREEN), true);
                            return 1;
                        }))
                        .then(Commands.literal("show").executes(ctx -> {
                            var c = ConfigManager.get();
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "[Chaos Roll] interval=" + c.rollIntervalSeconds
                                            + "s, balance=" + c.balanceMode
                                            + ", preventDeath=" + c.preventDirectDeath
                                            + ", global=" + c.globalEventsEnabled
                                            + ", maxActive=" + c.maxActiveEffects)
                                    .withStyle(ChatFormatting.GRAY), false);
                            return 1;
                        })));
        dispatcher.register(root);
    }
}
