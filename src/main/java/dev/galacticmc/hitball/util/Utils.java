package dev.galacticmc.hitball.util;

import com.google.common.collect.ImmutableMap;
import dev.galacticmc.hitball.raytracing.AABB;
import dev.galacticmc.hitball.raytracing.Ray;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static final Random random = new Random();
    // Mapping of Spigot ChatColor to appropriate Java Color
    public static final Map<ChatColor, java.awt.Color> COLOR_MAPPINGS;

    static {

        // https://minecraft.gamepedia.com/Formatting_codes#Color_codes
        COLOR_MAPPINGS = ImmutableMap.<ChatColor, java.awt.Color>builder()
                .put(ChatColor.BLACK, new java.awt.Color(0, 0, 0))
                .put(ChatColor.DARK_BLUE, new java.awt.Color(0, 0, 170))
                .put(ChatColor.DARK_GREEN, new java.awt.Color(0, 170, 0))
                .put(ChatColor.DARK_AQUA, new java.awt.Color(0, 170, 170))
                .put(ChatColor.DARK_RED, new java.awt.Color(170, 0, 0))
                .put(ChatColor.DARK_PURPLE, new java.awt.Color(170, 0, 170))
                .put(ChatColor.GOLD, new java.awt.Color(255, 170, 0))
                .put(ChatColor.GRAY, new java.awt.Color(170, 170, 170))
                .put(ChatColor.DARK_GRAY, new java.awt.Color(85, 85, 85))
                .put(ChatColor.BLUE, new java.awt.Color(85, 85, 255))
                .put(ChatColor.GREEN, new java.awt.Color(85, 255, 85))
                .put(ChatColor.AQUA, new java.awt.Color(85, 255, 255))
                .put(ChatColor.RED, new java.awt.Color(255, 85, 85))
                .put(ChatColor.LIGHT_PURPLE, new java.awt.Color(255, 85, 255))
                .put(ChatColor.YELLOW, new java.awt.Color(255, 255, 85))
                .put(ChatColor.WHITE, new java.awt.Color(255, 255, 255))
                .build();
    }

    // Returns first player in the specified player's line of sight
    // up to max blocks away, or null if none.
    public static FallingBlock getTargetFallingBLock(Player player, int max) {
        List<FallingBlock> possible = player.getNearbyEntities(max, max, max).stream().filter(entity -> entity instanceof FallingBlock).map(entity -> (FallingBlock) entity).filter(player::hasLineOfSight).collect(Collectors.toList());
        Ray ray = Ray.from(player);
        double d = -1;
        FallingBlock closest = null;
        for (FallingBlock fallingBlock : possible) {
            double dis = AABB.from(fallingBlock).collidesD(ray, 0, max);
            if (dis != -1) {
                if (dis < d || d == -1) {
                    d = dis;
                    closest = fallingBlock;
                }
            }
        }
        return closest;
    }

    public static Player getRandomOnlinePlayer(World world, Player shouldntBe) {
        Player player = getRandomOnlinePlayer(world);
        if(player == shouldntBe || player.isInvisible()){
            player = getRandomOnlinePlayer(world, shouldntBe);
        }
        return player;
    }

    public static Player getRandomOnlinePlayer(World world) {
        List<Player> onlinePlayers = new ArrayList<>(world.getPlayers());

        if (onlinePlayers.isEmpty() || onlinePlayers.stream().filter(player -> player.getGameMode().equals(GameMode.SURVIVAL)).count() < 2) {
            return null; // No players are online
        }

        int randomIndex = random.nextInt(onlinePlayers.size());
        return onlinePlayers.get(randomIndex);
    }

    public static HashMap<String, String> createMap(String... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of arguments. Must be even.");
        }

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            String value = keyValuePairs[i + 1];
            map.put(key, value);
        }

        return map;
    }

    public static String rgbToHex(int rgb) {
        return Integer.toHexString(rgb).substring(2);
    }

    public static int hexToRgb(String hex) {
        return Integer.valueOf(hex, 16);
    }

    public static int getRgb(int red, int green, int blue) {
        return new java.awt.Color(red, green, blue).getRGB();
    }

    public static String formattedRgb(java.awt.Color color) {
        return "(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
    }

    public static ChatColor getClosestChatColor(java.awt.Color color) {
        ChatColor closest = null;
        int mark = 0;
        for (Map.Entry<ChatColor, java.awt.Color> entry : COLOR_MAPPINGS.entrySet()) {
            ChatColor key = entry.getKey();
            java.awt.Color value = entry.getValue();

            int diff = getDiff(value, color);
            if (closest == null || diff < mark) {
                closest = key;
                mark = diff;
            }
        }

        return closest;
    }

    // Algorithm to determine the difference between two colors, source:
    // https://stackoverflow.com/questions/27374550/how-to-compare-color-object-and-get-closest-color-in-an-color
    private static int getDiff(java.awt.Color color, java.awt.Color compare) {
        int a = color.getAlpha() - compare.getAlpha(),
                r = color.getRed() - compare.getRed(),
                g = color.getGreen() - compare.getGreen(),
                b = color.getBlue() - compare.getBlue();
        return a * a + r * r + g * g + b * b;
    }

}
