package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Supplier;

import static com.kumoe.SeasonShop.api.ModUtils.AVATAR_CACHE_DIR;
import static com.kumoe.SeasonShop.data.SSLangData.COMMAND_CLEAN_AVATAR_FAILED;
import static com.kumoe.SeasonShop.data.SSLangData.COMMAND_CLEAN_AVATAR_SUCCESS;

public class S2CClearAvatarPacket {

    public S2CClearAvatarPacket() {
    }

    public static S2CClearAvatarPacket decode(FriendlyByteBuf ignoredBuf) {
        return new S2CClearAvatarPacket();
    }


    public void clearPlayerAvatarCache() {
        Path path = FMLPaths.GAMEDIR.get().resolve(AVATAR_CACHE_DIR);
        if (Minecraft.getInstance().player != null) {
            try {
                if (Files.exists(path)) {
                    // Use walkFileTree to delete the directory and its contents recursively
                    Files.walkFileTree(path, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    Minecraft.getInstance().player.displayClientMessage(Component.translatable(COMMAND_CLEAN_AVATAR_SUCCESS.key()).withStyle(COMMAND_CLEAN_AVATAR_SUCCESS.format()), false);
                } else {
                    Minecraft.getInstance().player.displayClientMessage(Component.translatable(COMMAND_CLEAN_AVATAR_FAILED.key()).withStyle(COMMAND_CLEAN_AVATAR_FAILED.format()), false);
                }
            } catch (IOException e) {
                SeasonShop.logger().debug(e.toString());
            }
        }
    }

    public void encode(FriendlyByteBuf ignoredBuf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(this::clearPlayerAvatarCache);
        }
        ctx.get().setPacketHandled(true);
    }
}
