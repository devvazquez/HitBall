package dev.galacticmc.hitball.objects;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SelectSwordEvent extends Event {

    private final String newNamespace, previousNamespace;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public SelectSwordEvent(String previousNamespace, String newNamespace) {
        this.previousNamespace = previousNamespace;
        this.newNamespace = newNamespace;
    }

    public String getNew(){
        return newNamespace;
    }

    public String getOld(){
        return previousNamespace;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
