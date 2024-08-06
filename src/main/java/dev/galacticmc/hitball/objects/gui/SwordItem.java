package dev.galacticmc.hitball.objects.gui;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.SelectSwordEvent;
import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.Collections;

public class SwordItem extends AbstractItem implements Listener {

    private final HitBallPlugin plugin;
    private final String itemsAdderNamespaceId;
    private final ItemMeta itemMeta;
    private boolean selected = false;

    public SwordItem(HitBallPlugin plugin, String namespace, boolean shouldStartSelected) {
        this.plugin = plugin;
        this.itemsAdderNamespaceId = namespace;

        ItemStack stack = CustomStack.getInstance(itemsAdderNamespaceId).getItemStack();
        this.itemMeta = stack.getItemMeta().clone();

        this.selected = shouldStartSelected;
        updateMeta();
    }

    public void updateMeta(){
        Component lore;
        if(selected){
            lore = Component.text("En uso").color(NamedTextColor.GREEN);
        }else {
            lore = Component.text("Guardada").color(NamedTextColor.RED);
        }
        this.itemMeta.lore(Collections.singletonList(lore));
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemStack stack = CustomStack.getInstance(itemsAdderNamespaceId).getItemStack();
        stack.setItemMeta(itemMeta);
        return new ItemBuilder(stack).addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_DESTROYS,ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_UNBREAKABLE,ItemFlag.HIDE_POTION_EFFECTS);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        //HitBall player
        HitBallPlayer hitBallPlayer = plugin.getWorldManager().getHitBallPlayer(player);
        //Previous sword
        String previousNamespace = hitBallPlayer.getSelectedSword();
        //Call the event
        SelectSwordEvent selectSwordEvent = new SelectSwordEvent(previousNamespace, itemsAdderNamespaceId);
        hitBallPlayer.setSelectedSword(itemsAdderNamespaceId);
        Bukkit.getPluginManager().callEvent(selectSwordEvent);
        this.selected = true;
    }

    @EventHandler
    public void newSelect(SelectSwordEvent event){
        //If it's the same selected.
        if(event.getNew().equals(itemsAdderNamespaceId)) return;
        //This is the old
        if(event.getOld().equals(itemsAdderNamespaceId)){
            this.selected = false;
        }
        updateMeta();
        notifyWindows();
    }

}
