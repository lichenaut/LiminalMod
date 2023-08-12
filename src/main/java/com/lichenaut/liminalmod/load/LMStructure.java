package com.lichenaut.liminalmod.load;

import org.bukkit.generator.structure.Structure;

public class LMStructure {

    private final Structure structure;
    private final boolean abandoned;

    public LMStructure(Structure structure, boolean abandoned) {
        this.structure = structure;
        this.abandoned = abandoned;
    }

    public Structure getStructureType() {return structure;}
    public boolean getAbandoned() {return abandoned;}
}
