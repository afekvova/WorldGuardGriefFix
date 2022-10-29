package ru.overwrite.wggf.listeners;

import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import ru.overwrite.wggf.WorldGuardGriefFixPlugin;
import ru.overwrite.wggf.objects.ProtectedRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerListener implements Listener {

    WorldGuardGriefFixPlugin plugin;
    List<ProtectedRegion> protectedRegionList = new ArrayList<>();

    List<Material> fallingBlocks = Arrays.asList(Material.SAND,
            Material.GRAVEL,
            Material.ANVIL);

    public PlayerListener(WorldGuardGriefFixPlugin plugin) {
        this.plugin = plugin;
        this.loadProtectedRegion(plugin.getConfig());
    }

    public void loadProtectedRegion(FileConfiguration configuration) {
        if (!this.protectedRegionList.isEmpty())
            this.protectedRegionList.clear();

        for (String regionName : configuration.getConfigurationSection("regions").getKeys(false)) {
            String world = configuration.getString("regions." + regionName + ".world", "world");
            int x1 = configuration.getInt("regions." + regionName + ".x1", 0);
            int y1 = configuration.getInt("regions." + regionName + ".y1", 0);
            int z1 = configuration.getInt("regions." + regionName + ".z1", 0);
            int x2 = configuration.getInt("regions." + regionName + ".x2", 0);
            int y2 = configuration.getInt("regions." + regionName + ".y2", 0);
            int z2 = configuration.getInt("regions." + regionName + ".z2", 0);
            this.protectedRegionList.add(new ProtectedRegion(world, x1, y1, z1, x2, y2, z2));
        }
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        val block = event.getBlock();
        val to = event.getTo();

        if (!this.plugin.getConfig().getBoolean("fix-block-fall") || event.getEntityType() != EntityType.FALLING_BLOCK)
            return;

        if (this.fallingBlocks.contains(block.getType()) && to == Material.AIR)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        FileConfiguration config = plugin.getConfig();
        if (!this.plugin.getConfig().getBoolean("enable-pistons")) return;

        for (Block block : event.getBlocks())
            if (!config.getStringList("excluded-blocks").contains(block.getType().name()) && this.checkLocation(block.getLocation())) {
                event.setCancelled(false);
                return;
            }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRetract(BlockPistonRetractEvent event) {
        if (!this.plugin.getConfig().getBoolean("enable-pistons")) return;

        for (Block block : event.getBlocks())
            if (this.checkLocation(block.getLocation())) {
                event.setCancelled(false);
                return;
            }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (this.plugin.getConfig().getBoolean("enable-minecart"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityChangeEvent(EntityChangeBlockEvent event) {
        event.setCancelled(false);

        if (plugin.getConfig().getStringList("excluded-blocks")
                .stream().map(String::toLowerCase).toList()
                .contains(event.getBlock().getType().name().toLowerCase()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitherBlockDamage(EntityChangeBlockEvent event) {
        if (!plugin.getConfig().getStringList("excluded-blocks")
                .stream().map(String::toLowerCase).toList()
                .contains(event.getBlock().getType().name().toLowerCase()) && this.checkLocation(event.getBlock().getLocation()) && event.getEntityType() == EntityType.WITHER && this.plugin.getConfig().getBoolean("enable-wither"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.checkLocation(event.getLocation()) && this.plugin.getConfig().getBoolean("enable-any-explotions"))
            event.setCancelled(false);

        if (this.checkLocation(event.getLocation()) && event.getEntityType() == EntityType.WITHER_SKULL && this.plugin.getConfig().getBoolean("enable-wither-skull")) {
            event.setCancelled(false);
            Location location = event.getEntity().getLocation();
            location.getWorld().createExplosion(location, 1);
        }
    }

    private boolean checkLocation(Location location) {
        if (location == null) return false;

        for (ProtectedRegion protectedRegion : this.protectedRegionList)
            if (protectedRegion.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                return false;

        return true;
    }

    @EventHandler
    public void onCreeperExplosion(EntityExplodeEvent event) {
        if (event.getEntityType() != EntityType.CREEPER) return;

        if (this.checkLocation(event.getLocation()) && this.plugin.getConfig().getBoolean("enable-any-explotions")) {
            Creeper creeper = (Creeper) event.getEntity();
            event.blockList().clear();
            event.getEntity().getWorld().createExplosion(event.getLocation(), creeper.isPowered() ? 6 : 3);
        }
    }
}