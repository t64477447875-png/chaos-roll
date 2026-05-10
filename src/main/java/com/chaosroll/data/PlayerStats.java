package com.chaosroll.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PlayerStats {
    public int totalRolls;
    public int positiveCount;
    public int negativeCount;
    public int chaoticCount;
    public int positiveStreakBest;
    public int negativeStreakBest;
    public int consecutivePositive;
    public int consecutiveNegative;
    public int morphCount;
    public int tornadoSurvived;
    public int screenFlippedCount;
    public int guardianUsed;
    public int voteSkippedCount;
    public final Map<String, Integer> byEventId = new HashMap<>();
    public final Set<String> achievements = new HashSet<>();
    public final Set<String> blocklist = new HashSet<>();

    public CompoundTag toNbt() {
        CompoundTag t = new CompoundTag();
        t.putInt("total", totalRolls);
        t.putInt("pos", positiveCount);
        t.putInt("neg", negativeCount);
        t.putInt("cha", chaoticCount);
        t.putInt("posBest", positiveStreakBest);
        t.putInt("negBest", negativeStreakBest);
        t.putInt("posCons", consecutivePositive);
        t.putInt("negCons", consecutiveNegative);
        t.putInt("morph", morphCount);
        t.putInt("tornado", tornadoSurvived);
        t.putInt("flip", screenFlippedCount);
        t.putInt("guardian", guardianUsed);
        t.putInt("voteSkip", voteSkippedCount);
        ListTag list = new ListTag();
        for (Map.Entry<String, Integer> e : byEventId.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("id", e.getKey());
            entry.putInt("count", e.getValue());
            list.add(entry);
        }
        t.put("events", list);

        ListTag achievementList = new ListTag();
        for (String a : achievements) {
            CompoundTag entry = new CompoundTag();
            entry.putString("id", a);
            achievementList.add(entry);
        }
        t.put("achievements", achievementList);

        ListTag blockList = new ListTag();
        for (String b : blocklist) {
            CompoundTag entry = new CompoundTag();
            entry.putString("id", b);
            blockList.add(entry);
        }
        t.put("blocklist", blockList);

        return t;
    }

    public static PlayerStats fromNbt(CompoundTag t) {
        PlayerStats s = new PlayerStats();
        s.totalRolls = t.getInt("total");
        s.positiveCount = t.getInt("pos");
        s.negativeCount = t.getInt("neg");
        s.chaoticCount = t.getInt("cha");
        s.positiveStreakBest = t.getInt("posBest");
        s.negativeStreakBest = t.getInt("negBest");
        s.consecutivePositive = t.getInt("posCons");
        s.consecutiveNegative = t.getInt("negCons");
        s.morphCount = t.getInt("morph");
        s.tornadoSurvived = t.getInt("tornado");
        s.screenFlippedCount = t.getInt("flip");
        s.guardianUsed = t.getInt("guardian");
        s.voteSkippedCount = t.getInt("voteSkip");
        ListTag list = t.getList("events", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            s.byEventId.put(e.getString("id"), e.getInt("count"));
        }
        ListTag achievementList = t.getList("achievements", Tag.TAG_COMPOUND);
        for (int i = 0; i < achievementList.size(); i++) {
            CompoundTag e = achievementList.getCompound(i);
            s.achievements.add(e.getString("id"));
        }
        ListTag blockList = t.getList("blocklist", Tag.TAG_COMPOUND);
        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag e = blockList.getCompound(i);
            s.blocklist.add(e.getString("id"));
        }
        return s;
    }
}
