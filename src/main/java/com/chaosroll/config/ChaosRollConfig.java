package com.chaosroll.config;

import java.util.ArrayList;
import java.util.List;

public final class ChaosRollConfig {

    public int rollIntervalSeconds = 60;
    public String rollKey = "R";
    public String balanceMode = "balanced";
    public int positiveWeight = 30;
    public int negativeWeight = 40;
    public int chaoticWeight = 30;
    public boolean enablePositiveEvents = true;
    public boolean enableNegativeEvents = true;
    public boolean enableChaoticEvents = true;
    public List<String> disabledEventIds = new ArrayList<>();
    public boolean preventDirectDeath = false;
    public int guaranteePositiveAfterNegativeStreak = 3;
    public int guaranteeNonPositiveAfterPositiveStreak = 2;
    public boolean showTimerBar = true;
    public boolean showActiveEffectsPanel = true;
    public boolean enableRollAnimation = true;
    public boolean enableSounds = true;
    public boolean enableParticles = true;
    public boolean globalEventsEnabled = true;
    public int maxActiveEffects = 5;
    public boolean enableDifficultyScaling = true;
    public boolean autoRoll = false;

    public ChaosRollConfig copy() {
        ChaosRollConfig c = new ChaosRollConfig();
        c.rollIntervalSeconds = rollIntervalSeconds;
        c.rollKey = rollKey;
        c.balanceMode = balanceMode;
        c.positiveWeight = positiveWeight;
        c.negativeWeight = negativeWeight;
        c.chaoticWeight = chaoticWeight;
        c.enablePositiveEvents = enablePositiveEvents;
        c.enableNegativeEvents = enableNegativeEvents;
        c.enableChaoticEvents = enableChaoticEvents;
        c.disabledEventIds = new ArrayList<>(disabledEventIds);
        c.preventDirectDeath = preventDirectDeath;
        c.guaranteePositiveAfterNegativeStreak = guaranteePositiveAfterNegativeStreak;
        c.guaranteeNonPositiveAfterPositiveStreak = guaranteeNonPositiveAfterPositiveStreak;
        c.showTimerBar = showTimerBar;
        c.showActiveEffectsPanel = showActiveEffectsPanel;
        c.enableRollAnimation = enableRollAnimation;
        c.enableSounds = enableSounds;
        c.enableParticles = enableParticles;
        c.globalEventsEnabled = globalEventsEnabled;
        c.maxActiveEffects = maxActiveEffects;
        c.enableDifficultyScaling = enableDifficultyScaling;
        c.autoRoll = autoRoll;
        return c;
    }

    public void validate() {
        ChaosRollConfig defaults = new ChaosRollConfig();
        if (rollIntervalSeconds < 5 || rollIntervalSeconds > 3600) rollIntervalSeconds = defaults.rollIntervalSeconds;
        if (rollKey == null || rollKey.isBlank()) rollKey = defaults.rollKey;
        if (balanceMode == null || !isValidBalanceMode(balanceMode)) balanceMode = defaults.balanceMode;
        if (positiveWeight < 0) positiveWeight = defaults.positiveWeight;
        if (negativeWeight < 0) negativeWeight = defaults.negativeWeight;
        if (chaoticWeight < 0) chaoticWeight = defaults.chaoticWeight;
        if (positiveWeight + negativeWeight + chaoticWeight == 0) {
            positiveWeight = defaults.positiveWeight;
            negativeWeight = defaults.negativeWeight;
            chaoticWeight = defaults.chaoticWeight;
        }
        if (disabledEventIds == null) disabledEventIds = new ArrayList<>();
        if (guaranteePositiveAfterNegativeStreak < 0) guaranteePositiveAfterNegativeStreak = defaults.guaranteePositiveAfterNegativeStreak;
        if (guaranteeNonPositiveAfterPositiveStreak < 0) guaranteeNonPositiveAfterPositiveStreak = defaults.guaranteeNonPositiveAfterPositiveStreak;
        if (maxActiveEffects < 1 || maxActiveEffects > 32) maxActiveEffects = defaults.maxActiveEffects;
    }

    private static boolean isValidBalanceMode(String mode) {
        return mode.equals("balanced")
                || mode.equals("more_positive")
                || mode.equals("more_negative")
                || mode.equals("pure_chaos");
    }
}
