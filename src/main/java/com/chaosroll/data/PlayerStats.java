package com.chaosroll.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;

public final class PlayerStats {
    public int totalRolls;
    public int positiveCount;
    public int negativeCount;
    public int chaoticCount;
    public final Map<String, Integer> byEventId = new HashMap<>();

    public CompoundTag toNbt() {
        CompoundTag t = new CompoundTag();
        t.putInt("total", totalRolls);
        t.putInt("pos", positiveCount);
        t.putInt("neg", negativeCount);
        t.putInt("cha", chaoticCount);
        ListTag list = new ListTag();
        for (Map.Entry<String, Integer> e : byEventId.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("id", e.getKey());
            entry.putInt("count", e.getValue());
            list.add(entry);
        }
        t.put("events", list);
        return t;
    }

    public static PlayerStats fromNbt(CompoundTag t) {
        PlayerStats s = new PlayerStats();
        s.totalRolls = t.getInt("total");
        s.positiveCount = t.getInt("pos");
        s.negativeCount = t.getInt("neg");
        s.chaoticCount = t.getInt("cha");
        ListTag list = t.getList("events", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            s.byEventId.put(e.getString("id"), e.getInt("count"));
        }
        return s;
    }
}
