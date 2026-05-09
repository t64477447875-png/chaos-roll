package com.chaosroll.data;

import com.chaosroll.ChaosRollMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.util.datafix.DataFixTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ChaosRollSavedData extends SavedData {

    private static final String SAVE_NAME = "chaosroll_data";

    public final Map<UUID, Integer> negativeStreak = new HashMap<>();
    public final Map<UUID, Integer> positiveStreak = new HashMap<>();
    public final Map<UUID, PlayerStats> stats = new HashMap<>();

    public static final Factory<ChaosRollSavedData> FACTORY = new Factory<>(
            ChaosRollSavedData::new,
            ChaosRollSavedData::load,
            DataFixTypes.LEVEL
    );

    public ChaosRollSavedData() {}

    public PlayerStats getOrCreateStats(UUID id) {
        return stats.computeIfAbsent(id, u -> new PlayerStats());
    }

    public static ChaosRollSavedData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(FACTORY, SAVE_NAME);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag streakList = new ListTag();
        for (Map.Entry<UUID, Integer> e : negativeStreak.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putUUID("uuid", e.getKey());
            t.putInt("count", e.getValue());
            streakList.add(t);
        }
        tag.put("negativeStreak", streakList);

        ListTag posList = new ListTag();
        for (Map.Entry<UUID, Integer> e : positiveStreak.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putUUID("uuid", e.getKey());
            t.putInt("count", e.getValue());
            posList.add(t);
        }
        tag.put("positiveStreak", posList);

        ListTag statsList = new ListTag();
        for (Map.Entry<UUID, PlayerStats> e : stats.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putUUID("uuid", e.getKey());
            t.put("data", e.getValue().toNbt());
            statsList.add(t);
        }
        tag.put("stats", statsList);

        return tag;
    }

    public static ChaosRollSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ChaosRollSavedData data = new ChaosRollSavedData();
        try {
            ListTag streakList = tag.getList("negativeStreak", Tag.TAG_COMPOUND);
            for (int i = 0; i < streakList.size(); i++) {
                CompoundTag t = streakList.getCompound(i);
                data.negativeStreak.put(t.getUUID("uuid"), t.getInt("count"));
            }
            ListTag posList = tag.getList("positiveStreak", Tag.TAG_COMPOUND);
            for (int i = 0; i < posList.size(); i++) {
                CompoundTag t = posList.getCompound(i);
                data.positiveStreak.put(t.getUUID("uuid"), t.getInt("count"));
            }
            ListTag statsList = tag.getList("stats", Tag.TAG_COMPOUND);
            for (int i = 0; i < statsList.size(); i++) {
                CompoundTag t = statsList.getCompound(i);
                UUID id = t.getUUID("uuid");
                PlayerStats st = PlayerStats.fromNbt(t.getCompound("data"));
                data.stats.put(id, st);
            }
        } catch (Exception ex) {
            ChaosRollMod.LOGGER.warn("[Chaos Roll] Failed to load saved data: {}", ex.getMessage());
        }
        return data;
    }
}
