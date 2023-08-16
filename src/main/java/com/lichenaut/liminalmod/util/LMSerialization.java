package com.lichenaut.liminalmod.util;

import com.google.flatbuffers.FlatBufferBuilder;
import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.load.LMStructure;
import com.lichenaut.liminalmod.load.LiminalMod.ChunkData;
import com.lichenaut.liminalmod.load.LiminalMod.Holistic;
import com.lichenaut.liminalmod.load.LiminalMod.LMStructureData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class LMSerialization {

    private final LiminalMod plugin;

    public LMSerialization(LiminalMod plugin) {this.plugin = plugin;}

    public void serializeStructures(HashMap<Chunk, HashSet<LMStructure>> dataMap) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);

        // Create a list of offsets for each ChunkData
        int[] chunkOffsets = new int[dataMap.size()];
        int index = 0;

        for (Map.Entry<Chunk, HashSet<LMStructure>> entry : dataMap.entrySet()) {
            Chunk chunk = entry.getKey();
            HashSet<LMStructure> structures = entry.getValue();

            // Create LMStructure array
            int[] lmStructureOffsets = new int[structures.size()];
            int lmIndex = 0;
            for (LMStructure structure : structures) {
                int structureStrOffset = builder.createString(structure.getStructure().getStructureType().toString());
                lmStructureOffsets[lmIndex] = com.lichenaut.liminalmod.load.LiminalMod.LMStructureData.createLMStructureData(builder, structureStrOffset, structure.isAbandoned());
                lmIndex++;
            }

            int structuresVector = ChunkData.createStructuresVector(builder, lmStructureOffsets);
            chunkOffsets[index] = ChunkData.createChunkData(builder, builder.createString(chunk.getWorld().getName()), chunk.getX(), chunk.getZ(), structuresVector);
            index++;
        }

        int chunksVector = Holistic.createChunksVector(builder, chunkOffsets);
        int holistic = Holistic.createHolistic(builder, chunksVector);

        // Finish up the serialization process
        builder.finish(holistic);

        ByteBuffer dataBuffer = builder.dataBuffer();

        try (FileOutputStream fos = new FileOutputStream("structureChunks.txt")) {
            fos.write(dataBuffer.array(), dataBuffer.position(), dataBuffer.remaining());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<Chunk, HashSet<LMStructure>> deserializeStructures() {
        HashMap<Chunk, HashSet<LMStructure>> dataMap = new HashMap<>();

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get("structureChunks.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return dataMap;  // Return empty map on failure
        }

        ByteBuffer bb = ByteBuffer.wrap(bytes);
        Holistic holistic = Holistic.getRootAsHolistic(bb);

        for (int i = 0; i < holistic.chunksLength(); i++) {
            ChunkData chunkData = holistic.chunks(i);

            String worldName = chunkData.world();
            if (worldName == null) return dataMap; // Return empty map on failure
            World world = Bukkit.getWorld(worldName);
            if (world == null) return dataMap; // Return empty map on failure

            Chunk chunk = world.getChunkAt(chunkData.x(), chunkData.z());
            HashSet<LMStructure> structuresSet = new HashSet<>();

            for (int j = 0; j < chunkData.structuresLength(); j++) {
                LMStructureData lmStruct = chunkData.structures(j);
                structuresSet.add(new LMStructure(new LMListenerUtil(plugin).getStructureByName(Objects.requireNonNull(lmStruct.structure())), lmStruct.abandoned()));
            }

            dataMap.put(chunk, structuresSet);
        }

        return dataMap;
    }
}