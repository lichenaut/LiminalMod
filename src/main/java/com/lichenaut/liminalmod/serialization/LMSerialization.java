package com.lichenaut.liminalmod.serialization;

import com.google.flatbuffers.FlatBufferBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LMSerialization {

    public static List<org.bukkit.Location> deserializeMarkers(ByteBuffer bb) {
        AbandonmentMarkers abandonmentMarkers = AbandonmentMarkers.getRootAsAbandonmentMarkers(bb);
        List<org.bukkit.Location> ls = new ArrayList<>();
        for (int i = 0; i < abandonmentMarkers.markersLength(); i++) {
            com.lichenaut.liminalmod.serialization.Location lData = abandonmentMarkers.markers(i);
            World w = Bukkit.getWorld(Objects.requireNonNull(lData.world()));
            if (w == null) continue;
            ls.add(new org.bukkit.Location(w, lData.x(), lData.y(), lData.z()));
        }
        return ls;
    }

    public static ByteBuffer serializeMarkers(List<Location> ls) {
        FlatBufferBuilder builder = new FlatBufferBuilder();

        int[] locationOffsets = new int[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            org.bukkit.Location l = ls.get(i);
            int worldOffset = builder.createString(l.getWorld().getName());
            locationOffsets[i] = com.lichenaut.liminalmod.serialization.Location.createLocation(builder, worldOffset, l.getX(), l.getY(), l.getZ());
        }
        int locationsVector = AbandonmentMarkers.createMarkersVector(builder, locationOffsets);

        AbandonmentMarkers.startAbandonmentMarkers(builder);
        AbandonmentMarkers.addMarkers(builder, locationsVector);
        int locationList = AbandonmentMarkers.endAbandonmentMarkers(builder);
        builder.finish(locationList);
        return builder.dataBuffer();
    }
}
