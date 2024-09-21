package dev.galacticmc.hitball.objects.gui;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.gui.skills.SelectedSkillItem;
import dev.galacticmc.hitball.objects.gui.skills.SkillItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    public Window provideSkillsWindow(HitBallPlayer hitBallPlayer){
        //Bukkit player
        Player player = hitBallPlayer.getSelf();

        //Gui border
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(""));

        //Stream will only be mapped if there are elements --> current skill shouldn't be null?
        List<Item> items = hitBallPlayer.getSkills().stream()
                .map(skill -> {
                    var item = new SkillItem(plugin, skill, hitBallPlayer.getCurrentSkill().getPermission().equals(skill.getPermission()));
                    plugin.getServer().getPluginManager().registerEvents(item, plugin);
                    return item;
                })
                .collect(Collectors.toList());

        //Register the selected item
        SelectedSkillItem selectedSwordItem = new SelectedSkillItem(hitBallPlayer);
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
                .setTitle("Seleccionar Habilidad")
                .build(player);
    }


}
