package com.lichenaut.liminalmod.load;

public class LMStructureProperties {
    private final int spawnRate;
    private final int abandonedRate;
    private final int abandonLootRate;

    public LMStructureProperties(int spawnRate, int abandonedRate, int abandonLootRate) {
        this.spawnRate = spawnRate;
        this.abandonedRate = abandonedRate;
        this.abandonLootRate = abandonLootRate;
    }

    public int getSpawnRate() {return spawnRate;}
    public int getAbandonedRate() {return abandonedRate;}
    public int getAbandonLootRate() {return abandonLootRate;}
}
