package com.whh.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.whh.config.ModConfig;
import com.whh.util.ModLootTableModifiers;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

/**
 * 注册 /whhloot reload 命令
 */
public class CommandRegistry {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(CommandManager.literal("whhloot")
                    .then(CommandManager.literal("reload")
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();

                                // 重新加载配置
                                ModConfig.reload();

                                // 重新注册 LootTable 修改（可选，如果希望实时刷新）
                                ModLootTableModifiers.modifyLootTable();

                                source.sendFeedback(() -> Text.literal("config已重新加载"), true);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );
        });
    }
}
