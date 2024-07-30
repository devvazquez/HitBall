package dev.galacticmc.hitball.objects.swords;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;

public class Sword {

    final String customItemName;

    public Sword(String customItemName) {
        this.customItemName = customItemName;
    }

    public void giveSwordToPlayer(Player player){
        CustomStack customStack = CustomStack.getInstance(customItemName);
        //Center spot
        player.getInventory().setItem(4, customStack.getItemStack());
    }

}