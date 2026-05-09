package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaDuelEvent extends BaseEvent {

    private static final int ARENA_RADIUS = 6;
    private static final int ARENA_HEIGHT = 6;
    private static final int ARENA_Y = 240;
    private static final int DUEL_TICKS = 1200; // 60s
    private static final int DUEL_OFFSET = 220;

    @Override public String getId() { return "coop_arena_duel"; }
    @Override public String getDisplayName() { return "Дуельна арена"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return DUEL_TICKS; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> others = CoopUtil.otherPlayers(initiator);
        if (others.isEmpty()) return;
        ServerPlayer opponent = others.get(context.random().nextInt(others.size()));

        ServerLevel arenaLevel = initiator.serverLevel();
        BlockPos origin = initiator.blockPosition();

        double angle = context.random().nextDouble() * Math.PI * 2.0;
        int cx = origin.getX() + (int) (Math.cos(angle) * DUEL_OFFSET);
        int cz = origin.getZ() + (int) (Math.sin(angle) * DUEL_OFFSET);
        BlockPos arenaCenter = new BlockPos(cx, ARENA_Y, cz);

        DuelSession session = new DuelSession(
                initiator.getUUID(), opponent.getUUID(),
                initiator.position().x, initiator.position().y, initiator.position().z,
                opponent.position().x, opponent.position().y, opponent.position().z,
                arenaLevel, arenaCenter,
                initiator.getHealth(), opponent.getHealth(),
                new ArrayList<>());
        ACTIVE.add(session);

        buildArena(arenaLevel, arenaCenter, session.placed);

        teleportToCorner(initiator, arenaLevel, arenaCenter, true);
        teleportToCorner(opponent, arenaLevel, arenaCenter, false);

        giveDuelKit(initiator);
        giveDuelKit(opponent);
        applyDuelEffects(initiator);
        applyDuelEffects(opponent);

        EventNotifyUtil.notifyAll(initiator, this,
                "ДУЕЛЬ! " + initiator.getName().getString() + " проти "
                        + opponent.getName().getString()
                        + ". 60с. Не битися = програш = 1 HP + Slowness V.");

        UUID sid = session.id;
        ScheduledTaskManager.schedule(context.server(), DUEL_TICKS, srv -> finishDuel(srv, sid));
    }

    private static void buildArena(ServerLevel level, BlockPos center, List<BlockPos> placed) {
        for (int dx = -ARENA_RADIUS; dx <= ARENA_RADIUS; dx++) {
            for (int dz = -ARENA_RADIUS; dz <= ARENA_RADIUS; dz++) {
                BlockPos floor = center.offset(dx, 0, dz);
                level.setBlock(floor, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                placed.add(floor);

                BlockPos ceil = center.offset(dx, ARENA_HEIGHT, dz);
                level.setBlock(ceil, Blocks.BARRIER.defaultBlockState(), 3);
                placed.add(ceil);
            }
        }
        for (int dy = 1; dy < ARENA_HEIGHT; dy++) {
            for (int side = -ARENA_RADIUS; side <= ARENA_RADIUS; side++) {
                BlockPos n = center.offset(side, dy, -ARENA_RADIUS);
                BlockPos s = center.offset(side, dy, ARENA_RADIUS);
                BlockPos w = center.offset(-ARENA_RADIUS, dy, side);
                BlockPos e = center.offset(ARENA_RADIUS, dy, side);
                level.setBlock(n, Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(s, Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(w, Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(e, Blocks.GLASS.defaultBlockState(), 3);
                placed.add(n);
                placed.add(s);
                placed.add(w);
                placed.add(e);
            }
        }
        BlockPos[] corners = {
                center.offset(ARENA_RADIUS, 1, ARENA_RADIUS),
                center.offset(-ARENA_RADIUS, 1, ARENA_RADIUS),
                center.offset(ARENA_RADIUS, 1, -ARENA_RADIUS),
                center.offset(-ARENA_RADIUS, 1, -ARENA_RADIUS),
        };
        for (BlockPos c : corners) {
            level.setBlock(c, Blocks.SEA_LANTERN.defaultBlockState(), 3);
            placed.add(c);
        }
    }

    private static void teleportToCorner(ServerPlayer p, ServerLevel level, BlockPos center, boolean north) {
        int dx = north ? -ARENA_RADIUS + 1 : ARENA_RADIUS - 1;
        int dz = north ? -ARENA_RADIUS + 1 : ARENA_RADIUS - 1;
        p.teleportTo(center.getX() + dx + 0.5, center.getY() + 1, center.getZ() + dz + 0.5);
    }

    private static void giveDuelKit(ServerPlayer p) {
        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        try {
            EventEnchantUtil.enchant(p.serverLevel(), sword, Enchantments.SHARPNESS, 3);
            EventEnchantUtil.enchant(p.serverLevel(), sword, Enchantments.UNBREAKING, 3);
        } catch (Throwable ignored) {}
        sword.set(DataComponents.CUSTOM_NAME,
                Component.literal("⚔ Меч дуелі ⚔").withStyle(ChatFormatting.RED));
        CoopTickHandler.applyMarker(sword, CoopTickHandler.NBT_DUEL_KIT);
        InventoryUtil.giveOrDrop(p, sword);

        ItemStack apple = new ItemStack(Items.GOLDEN_APPLE, 2);
        CoopTickHandler.applyMarker(apple, CoopTickHandler.NBT_DUEL_KIT);
        InventoryUtil.giveOrDrop(p, apple);
    }

    private static void applyDuelEffects(ServerPlayer p) {
        p.setHealth(p.getMaxHealth());
        p.getFoodData().setFoodLevel(20);
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DUEL_TICKS, 1, true, false, true));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DUEL_TICKS, 0, true, false, true));
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, DUEL_TICKS, 0, true, false, true));
        p.addEffect(new MobEffectInstance(MobEffects.GLOWING, DUEL_TICKS, 0, true, false, true));
    }

    private static void finishDuel(MinecraftServer server, UUID sessionId) {
        DuelSession session = null;
        for (DuelSession s : ACTIVE) {
            if (s.id.equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (session == null) return;
        ACTIVE.remove(session);

        ServerPlayer pa = server.getPlayerList().getPlayer(session.a);
        ServerPlayer pb = server.getPlayerList().getPlayer(session.b);

        ServerPlayer loser = null;
        boolean noFight = false;
        if (pa != null && pb != null) {
            float lostA = Math.max(0f, session.startHpA - pa.getHealth());
            float lostB = Math.max(0f, session.startHpB - pb.getHealth());
            if (lostA < 0.5f && lostB < 0.5f) {
                noFight = true;
                loser = (server.overworld().getRandom().nextBoolean()) ? pa : pb;
            } else if (pa.getHealth() < pb.getHealth()) {
                loser = pa;
            } else if (pb.getHealth() < pa.getHealth()) {
                loser = pb;
            }
        }

        for (ServerPlayer p : new ServerPlayer[]{pa, pb}) {
            if (p == null) continue;
            stripDuelKit(p);
            p.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            p.removeEffect(MobEffects.DAMAGE_BOOST);
            p.removeEffect(MobEffects.REGENERATION);
            p.removeEffect(MobEffects.GLOWING);
        }

        if (pa != null) pa.teleportTo(session.startAX, session.startAY, session.startAZ);
        if (pb != null) pb.teleportTo(session.startBX, session.startBY, session.startBZ);

        if (loser != null) {
            float punish = noFight ? 5.0f : 5.0f;
            loser.setHealth(Math.max(1.0f, loser.getMaxHealth() - punish * 4));
            loser.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4));
            if (noFight) {
                loser.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
                loser.setHealth(1.0f);
            }
            String reason = noFight
                    ? "відмовилися битися — кара (1 HP, Slowness V, Blindness 10с)"
                    : "програв дуель (низьке HP, Slowness V 10с)";
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] " + loser.getName().getString() + " " + reason)
                            .withStyle(ChatFormatting.RED), false);
        } else {
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] Дуель завершилася в нічию.")
                            .withStyle(ChatFormatting.YELLOW), false);
        }

        for (BlockPos pos : session.placed) {
            session.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void stripDuelKit(ServerPlayer p) {
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (CoopTickHandler.hasMarker(stack, CoopTickHandler.NBT_DUEL_KIT)) {
                inv.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static final List<DuelSession> ACTIVE = new ArrayList<>();

    private static final class DuelSession {
        final UUID id = UUID.randomUUID();
        final UUID a;
        final UUID b;
        final double startAX, startAY, startAZ;
        final double startBX, startBY, startBZ;
        final ServerLevel level;
        final BlockPos arenaCenter;
        final float startHpA;
        final float startHpB;
        final List<BlockPos> placed;

        DuelSession(UUID a, UUID b,
                    double ax, double ay, double az,
                    double bx, double by, double bz,
                    ServerLevel level, BlockPos arenaCenter,
                    float startHpA, float startHpB,
                    List<BlockPos> placed) {
            this.a = a;
            this.b = b;
            this.startAX = ax;
            this.startAY = ay;
            this.startAZ = az;
            this.startBX = bx;
            this.startBY = by;
            this.startBZ = bz;
            this.level = level;
            this.arenaCenter = arenaCenter;
            this.startHpA = startHpA;
            this.startHpB = startHpB;
            this.placed = placed;
        }
    }
}
