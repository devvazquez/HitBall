package dev.galacticmc.hitball.objects.gui;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.stream.Collectors;

public class GuiProvider {

    private final HitBallPlugin plugin;

    public GuiProvider(HitBallPlugin plugin) {
        this.plugin = plugin;
    }

    public Window provideSwordsWindow(HitBallPlayer hitBallPlayer){
        //Bukkit player
        Player player = hitBallPlayer.getSelf();

        //Gui border
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(""));

        // an example list of items to display
        List<Item> items = plugin.getDatabase().getPlayerSwords(player.getUniqueId()).stream()
                .map(namespace -> new SwordItem(plugin, namespace, hitBallPlayer.getSelectedSword().equals(namespace)))
                .collect(Collectors.toList());

        //Register the events for the items
        items.forEach(item -> {
            plugin.getServer().getPluginManager().registerEvents((Listener) item, plugin);
        });

        //Register the selected item
        SelectedSwordItem selectedSwordItem = new SelectedSwordItem(hitBallPlayer);
        plugin.getServer().getPluginManager().registerEvents(selectedSwordItem, plugin);

        //Create the gui
        Gui gui = PagedGui.items()
                .setStructure(
                        "# # # # # # # # #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# # # < y > # # #")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('#', border)
                .addIngredient('y', selectedSwordItem)
                .addIngredient('<', new BackItem())
                .addIngredient('>', new ForwardItem())
                .setContent(items)
                .build();
        return Window.single()
                    .setGui(gui)
                    .setTitle("Seleccionar Espada")
                    .build(player);
    }

}
