package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.UUID;

public class CoopBossEvent extends BaseEvent {

    private static final ResourceLocation HP_MOD =
            ResourceLocation.fromNamespaceAndPath("chaosroll", "boss_hp");
    private static final ResourceLocation ATK_MOD =
            ResourceLocation.fromNamespaceAndPath("chaosroll", "boss_atk");

    @Override public String getId() { return "coop_boss"; }
    @Override public String getDisplayName() { return "Хаос-страж"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return 6000; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        IronGolem golem = EntityType.IRON_GOLEM.create(context.world());
        if (golem == null) {
            EventNotifyUtil.notifyAll(initiator, this, "Неможливо створити боса");
            return;
        }

        AttributeInstance hp = golem.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance atk = golem.getAttribute(Attributes.ATTACK_DAMAGE);
        if (hp != null) {
            hp.removeModifier(HP_MOD);
            hp.addPermanentModifier(new AttributeModifier(HP_MOD, 200.0,
                    AttributeModifier.Operation.ADD_VALUE));
        }
        if (atk != null) {
            atk.removeModifier(ATK_MOD);
            atk.addPermanentModifier(new AttributeModifier(ATK_MOD, 6.0,
                    AttributeModifier.Operation.ADD_VALUE));
        }
        golem.setHealth(golem.getMaxHealth());

        golem.setCustomName(Component.literal("✦ Хаос-страж ✦")
                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
        golem.setCustomNameVisible(true);
        golem.setPersistenceRequired();
        golem.setPos(initiator.getX() + 4, initiator.getY(), initiator.getZ() + 4);
        context.world().addFreshEntity(golem);

        UUID bossId = golem.getUUID();
        int rewardEndTick = context.server().getTickCount() + getDurationTicks();
        scheduleCheck(context.server(), bossId, rewardEndTick);

        EventNotifyUtil.notifyAll(initiator, this,
                "Хаос-страж з'явився! 5 хв на нього. Винагорода — 16 ізумрудів + тотем кожному.");
    }

    private static void scheduleCheck(net.minecraft.server.MinecraftServer server,
                                      UUID bossId, int endTick) {
        ScheduledTaskManager.schedule(server, 20, srv -> {
            int now = srv.getTickCount();
            net.minecraft.world.entity.Entity entity = null;
            for (net.minecraft.server.level.ServerLevel sl : srv.getAllLevels()) {
                entity = sl.getEntity(bossId);
                if (entity != null) break;
            }

            if (entity == null || !entity.isAlive()) {
                if (entity != null && !entity.isAlive()) {
                    awardPlayers(srv);
                } else {
                    srv.getPlayerList().broadcastSystemMessage(
                            Component.literal("[Chaos Roll] Хаос-страж зник.").withStyle(ChatFormatting.GRAY),
                            false);
                }
                return;
            }
            if (now >= endTick) {
                entity.discard();
                srv.getPlayerList().broadcastSystemMessage(
                        Component.literal("[Chaos Roll] Хаос-страж відкликаний (час вийшов).")
                                .withStyle(ChatFormatting.GRAY), false);
                return;
            }
            scheduleCheck(srv, bossId, endTick);
        });
    }

    private static void awardPlayers(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            InventoryUtil.giveOrDrop(p, new ItemStack(Items.EMERALD, 16));
            InventoryUtil.giveOrDrop(p, new ItemStack(Items.TOTEM_OF_UNDYING, 1));
        }
        server.getPlayerList().broadcastSystemMessage(
                Component.literal("[Chaos Roll] Хаос-страж переможений! +16 ізумрудів + 1 тотем кожному.")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
    }
}
