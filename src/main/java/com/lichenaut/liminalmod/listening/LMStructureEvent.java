package com.lichenaut.liminalmod.listening;

import org.bukkit.event.world.AsyncStructureSpawnEvent;

public class LMStructureEvent {//TODO: this is a mess

    private final AsyncStructureSpawnEvent e;
    private Runnable onCompletion;

    public LMStructureEvent(AsyncStructureSpawnEvent e) {this.e = e;}

    public AsyncStructureSpawnEvent getEvent() {return e;}
}