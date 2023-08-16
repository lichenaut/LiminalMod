package com.lichenaut.liminalmod.load;

import org.bukkit.generator.structure.Structure;

import java.io.Serializable;

public class LMStructure implements Serializable {

    private final Structure structure;
    private final boolean abandoned;

    public LMStructure(Structure structure, boolean abandoned) {
        this.structure = structure;
        this.abandoned = abandoned;
    }

    public Structure getStructure() {return structure;}
    public boolean isAbandoned() {return abandoned;}
}
