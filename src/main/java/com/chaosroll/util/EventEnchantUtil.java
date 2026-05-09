package com.chaosroll.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EventEnchantUtil {

    private EventEnchantUtil() {}

    public static Holder<Enchantment> get(ServerLevel level, ResourceKey<Enchantment> key) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    public static ItemStack enchant(ServerLevel level, ItemStack stack, ResourceKey<Enchantment> key, int lvl) {
        stack.enchant(get(level, key), lvl);
        return stack;
    }
}