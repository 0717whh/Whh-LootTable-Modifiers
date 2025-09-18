package com.whh.util;

import com.whh.config.ModConfig;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;

import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {

    public static ModConfig CONFIG;
    public static void initConfig() {
        CONFIG = ModConfig.load();
    }
    public static void modifyLootTable() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Identifier id = key.getValue();

            if (id.getPath().startsWith("chests/")||id.getPath().startsWith("spawners/")) {

                // 遍历配置中的每个物品
                for (ModConfig.Entry entry : CONFIG.entries) {
                    Identifier itemId = entry.item.contains(":")
                            ? Identifier.of(entry.item)
                            : Identifier.of("minecraft", entry.item);
                    Item item = Registries.ITEM.get(itemId);
                    if (item == Items.AIR) continue;

                    // 添加新的物品池
                    LootPool.Builder pool = LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .conditionally(RandomChanceLootCondition.builder(entry.chance))
                            .with(ItemEntry.builder(item))
                            .apply(SetCountLootFunction.builder(
                                    UniformLootNumberProvider.create(entry.min, entry.max)
                            ));

                    tableBuilder.pool(pool);
                }
            }

        });
    }
}

