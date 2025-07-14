package ru.overwrite.wggf;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.overwrite.wggf.listeners.PlayerListener;

public class WorldGuardGriefFixPlugin extends JavaPlugin {

    @Getter
    private PlayerListener listener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.listener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(this.listener, this);
        getCommand("wggf").setExecutor(new WorldGuardGriefFixCommand(this));
        new Metrics(this, 14247);
    }
}