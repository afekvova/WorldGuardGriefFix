package ru.overwrite.wggf;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardGriefFixPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        new Metrics(this, 14247);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (this.getConfig().getBoolean("enable-pistons"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRetract(BlockPistonRetractEvent event) {
        if (this.getConfig().getBoolean("enable-pistons"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (this.getConfig().getBoolean("enable-minecart"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitherBlockDamage(EntityDamageByBlockEvent event) {
        if (this.getConfig().getBoolean("enable-wither"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (this.getConfig().getBoolean("enable-sand"))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.getConfig().getBoolean("enable-any-explotions"))
            event.setCancelled(false);
    }
}

