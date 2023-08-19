package com.lichenaut.liminalmod.load;

public class LMStructureProperties {
    private final int spawnRate;
    private final int abandonedRate;
    private final int abandonLootRate;
    private final boolean transformStructure;

    public LMStructureProperties(int spawnRate, int abandonedRate, int abandonLootRate, boolean transformStructure) {
        this.spawnRate = spawnRate;
        this.abandonedRate = abandonedRate;
        this.abandonLootRate = abandonLootRate;
        this.transformStructure = transformStructure;
    }

    public int getSpawnRate() {return spawnRate;}
    public int getAbandonedRate() {return abandonedRate;}
    public int getLootAbandonRate() {return abandonLootRate;}
    public boolean getTransformStructure() {return transformStructure;}
}
