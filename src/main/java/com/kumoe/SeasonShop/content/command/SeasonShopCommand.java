package com.kumoe.SeasonShop.content.command;

import com.kumoe.SeasonShop.api.ModUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SeasonShopCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("season_shop")
                .then(Commands.literal("clearAvatarCache")
                        .requires(ctx -> ctx.hasPermission(2))
                        .executes(ModUtils::clearPlayerAvatarCache))
        );
    }
}