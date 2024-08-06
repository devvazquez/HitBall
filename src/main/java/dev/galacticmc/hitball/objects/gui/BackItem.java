package dev.galacticmc.hitball.objects.gui;

import org.bukkit.Material;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class BackItem extends PageItem {

    public BackItem() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(Material.RED_WOOL);
        builder.setDisplayName("Página anterior")
                .addLoreLines(gui.hasPreviousPage()
                        ? "Ir a la página " + gui.getCurrentPage() + "/" + gui.getPageAmount()
                        : "Es la primera página!");

        return builder;
    }

}
