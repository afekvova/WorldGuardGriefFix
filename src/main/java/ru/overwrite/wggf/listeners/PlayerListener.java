package ru.overwrite.wggf.listeners;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import ru.overwrite.wggf.WorldGuardGriefFixPlugin;
import ru.overwrite.wggf.objects.GriefConfig;
import ru.overwrite.wggf.objects.ProtectedRegion;

import java.util.*;

public class PlayerListener implements Listener {

    private final List<ProtectedRegion> protectedRegionList = new ArrayList<>();
    private final Set<Material> excludedBlocks = EnumSet.noneOf(Material.class);
    private final Set<String> excludedWorlds = new HashSet<>();
    @Getter
    private final GriefConfig griefConfig = new GriefConfig();

    public PlayerListener(WorldGuardGriefFixPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        this.loadProtectedRegion(configuration);
        this.loadExcludedBlocks(configuration);
        this.loadExcludedWorlds(configuration);
        this.griefConfig.loadConfig(configuration);
    }

    public void loadProtectedRegion(FileConfiguration configuration) {
        if (!this.protectedRegionList.isEmpty()) {
            this.protectedRegionList.clear();
        }
        for (String regionName : configuration.getConfigurationSection("regions").getKeys(false)) {
            int x1 = configuration.getInt("regions." + regionName + ".x1", 0);
            int y1 = configuration.getInt("regions." + regionName + ".y1", 0);
            int z1 = configuration.getInt("regions." + regionName + ".z1", 0);
            int x2 = configuration.getInt("regions." + regionName + ".x2", 0);
            int y2 = configuration.getInt("regions." + regionName + ".y2", 0);
            int z2 = configuration.getInt("regions." + regionName + ".z2", 0);
            protectedRegionList.add(new ProtectedRegion(x1, y1, z1, x2, y2, z2));
        }
    }

    public void loadExcludedBlocks(FileConfiguration configuration) {
        if (!excludedBlocks.isEmpty()) {
            excludedBlocks.clear();
        }
        for (String material : configuration.getStringList("excluded-blocks")) {
            excludedBlocks.add(Material.valueOf(material.toUpperCase()));
        }
    }

    public void loadExcludedWorlds(FileConfiguration configuration) {
        if (!excludedWorlds.isEmpty()) {
            excludedWorlds.clear();
        }
        excludedWorlds.addAll(configuration.getStringList("excluded-worlds"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!griefConfig.isEnablePistons()) {
            return;
        }
        for (Block block : event.getBlocks()) {
            if (!excludedBlocks.contains(block.getType()) && checkLocation(block.getLocation())) {
                event.setCancelled(false);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRetract(BlockPistonRetractEvent event) {
        if (!griefConfig.isEnablePistons()) {
            return;
        }
        for (Block block : event.getBlocks()) {
            if (this.checkLocation(block.getLocation())) {
                event.setCancelled(false);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (griefConfig.isEnableMinecart()) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitherBlockDamage(EntityChangeBlockEvent event) {
        if (griefConfig.isEnableWither() && event.getEntityType() == EntityType.WITHER) {
            event.setCancelled(false);
            if (excludedBlocks.contains(event.getBlock().getType())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW) // Спасибо, Витя
    public void onLowFallingBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock) || !griefConfig.isEnableFallingBlock()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitorFallingBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock) || !griefConfig.isEnableFallingBlock()) {
            return;
        }
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent event) {
        if (griefConfig.isEnableFishing()) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (checkLocation(event.getLocation()) && griefConfig.isEnableAnyExplosion()) {
            event.setCancelled(false);
        } else if (checkLocation(event.getLocation()) && griefConfig.isEnableWitherSkull() && event.getEntityType() == EntityType.WITHER_SKULL) {
            event.setCancelled(false);
        }
    }

    private boolean checkLocation(Location location) {
        if (location == null) {
            return false;
        }
        if (excludedWorlds.contains(location.getWorld().getName())) {
            return false;
        }
        for (ProtectedRegion protectedRegion : protectedRegionList) {
            if (protectedRegion.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                return false;
            }
        }
        return true;
    }
}