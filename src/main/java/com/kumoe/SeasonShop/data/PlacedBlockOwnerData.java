package com.kumoe.SeasonShop.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlacedBlockOwnerData extends SavedData {
    private static final String DATA_NAME = "PlacedBlockOwnersData";
    private final Map<UUID, Integer> placedBlockOwners = new HashMap<>();

    public static PlacedBlockOwnerData load(CompoundTag pCompoundTag) {
        ListTag listTag = pCompoundTag.getList("PlacedBlockCounts", ListTag.TAG_COMPOUND);
        PlacedBlockOwnerData data = new PlacedBlockOwnerData();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            UUID uuid = tag.getUUID("UUID");
            int count = tag.getInt("Count");
            data.placedBlockOwners.put(uuid, count);
        }
        return data;
    }

    public static PlacedBlockOwnerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PlacedBlockOwnerData::load, PlacedBlockOwnerData::new, DATA_NAME);
    }

    public int getCount(UUID uuid) {
        return placedBlockOwners.getOrDefault(uuid, 0);
    }

    public void setCount(UUID player, int count) {
        placedBlockOwners.put(player, count);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (Map.Entry<UUID, Integer> entry : placedBlockOwners.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            playerTag.putInt("Count", entry.getValue());
            listTag.add(playerTag);
        }
        tag.put("PlacedBlockCounts", listTag);
        return tag;
    }
}
