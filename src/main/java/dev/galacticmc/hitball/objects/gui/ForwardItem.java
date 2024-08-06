package dev.galacticmc.hitball.objects.gui;

import org.bukkit.Material;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class ForwardItem extends PageItem {

    public ForwardItem() {
        super(true);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE);
        builder.setDisplayName("Siguente página")
                .addLoreLines(gui.hasNextPage()
                        ? "Ir a la página " + (gui.getCurrentPage() + 2) + "/" + gui.getPageAmount()
                        : "Es la última página!");

        return builder;
    }

}
