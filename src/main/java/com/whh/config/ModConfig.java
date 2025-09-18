package com.whh.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * 配置管理类
 */
public class ModConfig {

    public static class Entry {
        public String item;
        public int min;
        public int max;
        public float chance;
    }

    public List<Entry> entries;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/whh_loot_config.json");

    private static ModConfig CONFIG;

    /** 初始化配置 */
    public static void initConfig() {
        reload();
    }

    /** 重新加载配置，如果文件不存在则自动创建默认 */
    public static void reload() {
        try {
            File configDir = FILE.getParentFile();
            if (!configDir.exists()) configDir.mkdirs();

            if (!FILE.exists()) {
                // 默认配置
                ModConfig defaultConfig = new ModConfig();
                defaultConfig.entries = List.of(
                        create("minecraft:tnt", 64, 64, 1.0f)
                );
                save(defaultConfig);
            }

            CONFIG = load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 获取当前配置 */
    public static ModConfig get() {
        if (CONFIG == null) initConfig();
        return CONFIG;
    }

    /** 加载配置文件 */
    public static ModConfig load() {
        try (FileReader reader = new FileReader(FILE)) {
            ModConfig config = GSON.fromJson(reader, ModConfig.class);

            // 防止 entries 为 null
            if (config.entries == null || config.entries.isEmpty()) {
                config.entries = List.of(create("minecraft:tnt", 64, 64, 1.0f));
            }

            return config;
        } catch (Exception e) {
            e.printStackTrace();
            ModConfig defaultConfig = new ModConfig();
            defaultConfig.entries = List.of(create("minecraft:tnt", 64, 64, 1.0f));
            return defaultConfig;
        }
    }

    /** 保存配置文件 */
    public static void save(ModConfig config) {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 创建一个配置条目 */
    public static Entry create(String item, int min, int max, float chance) {
        Entry e = new Entry();
        e.item = item;
        e.min = min;
        e.max = max;
        e.chance = chance;
        return e;
    }

    /** 根据条目获取 Item 对象 */
    public static Item getItem(Entry entry) {
        Identifier id = entry.item.contains(":") ? Identifier.of(entry.item) : Identifier.of("minecraft", entry.item);
        return Registries.ITEM.get(id);
    }
}
