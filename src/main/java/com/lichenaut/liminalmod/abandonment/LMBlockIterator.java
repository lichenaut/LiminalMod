package com.lichenaut.liminalmod.abandonment;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LMBlockIterator implements Iterator<Block> {

    private final BoundingBox box;
    private final World w;
    private int x, y, z;

    public LMBlockIterator(World w, BoundingBox box) {
        this.w = w;
        this.box = box;
        this.x = (int) box.getMinX();
        this.y = (int) box.getMinY();
        this.z = (int) box.getMinZ();
    }

    @Override
    public boolean hasNext() {return x <= box.getMaxX() && y <= box.getMaxY() && z <=  box.getMaxZ();}

    @Override
    public Block next() {
        if (!hasNext()) throw new NoSuchElementException();

        Block currentBlock = w.getBlockAt(x, y, z);

        x++;
        if (x > box.getMaxX()) {
            x = (int) box.getMinX();
            z++;
            if (z > box.getMaxZ()) {
                z = (int) box.getMinZ();
                y++;
            }
        }

        return currentBlock;
    }
}