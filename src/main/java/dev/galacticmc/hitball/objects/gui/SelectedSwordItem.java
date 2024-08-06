package dev.galacticmc.hitball.objects.gui;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.SelectSwordEvent;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class SelectedSwordItem extends AbstractItem implements Listener {

    private final HitBallPlayer player;

    public SelectedSwordItem(HitBallPlayer player) {
        this.player = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(CustomStack.getInstance(player.getSelectedSword()).getItemStack());
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {

    }

    @EventHandler
    public void playerSelectSword(SelectSwordEvent event){
        notifyWindows();
    }

}
