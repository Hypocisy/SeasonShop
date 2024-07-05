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
        PlacedBlockOwnerData data = new PlacedBlockOwnerData();
        ListTag listTag = pCompoundTag.getList("PlacedBlockOwners", ListTag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            UUID uuid = UUID.fromString(tag.getString("uuid"));
            int count = tag.getInt("count");
            data.placedBlockOwners.put(uuid, count);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        ListTag listTag = new ListTag();
        for (Map.Entry<UUID, Integer> entry : placedBlockOwners.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            playerTag.putInt("Count", entry.getValue());
            pCompoundTag.merge(playerTag);
        }
        return pCompoundTag;
    }

    public Map<UUID, Integer> getPlacedBlockOwners() {
        return placedBlockOwners;
    }

    public static PlacedBlockOwnerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PlacedBlockOwnerData::load,PlacedBlockOwnerData::new, DATA_NAME);
    }

}
