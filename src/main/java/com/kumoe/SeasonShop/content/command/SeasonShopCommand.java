package com.kumoe.SeasonShop.content.command;

import com.kumoe.SeasonShop.network.NetworkHandler;
import com.kumoe.SeasonShop.network.S2CClearAvatarPacket;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.PacketDistributor;

public class SeasonShopCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("season_shop")
                .then(Commands.literal("clearAvatarCache")
                        .requires(ctx -> ctx.hasPermission(2))
                        .executes(context -> {
                            if (context.getSource().isPlayer()) {
                                NetworkHandler.getInstance().send(PacketDistributor.PLAYER.with(() -> context.getSource().getPlayer()), new S2CClearAvatarPacket());
                                return 1;
                            } else {
                                context.getSource().sendSystemMessage(Component.literal("This command can only execute with player!"));
                                return 0;
                            }
                        }))
        );
    }
}