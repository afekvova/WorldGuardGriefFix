package ru.overwrite.wggf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class WorldGuardGriefFixCommand implements CommandExecutor {

    private final WorldGuardGriefFixPlugin plugin;

    public WorldGuardGriefFixCommand(WorldGuardGriefFixPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("wggf.command.reload")) {
            sender.sendMessage("§cУ вас нет разрешений!");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("§f[§6WGGF§f] /wggf reload - перезагрузить конфиг");
            return true;
        }

        plugin.reloadConfig();
        FileConfiguration configuration = plugin.getConfig();
        plugin.getListener().loadProtectedRegion(configuration);
        plugin.getListener().loadExcludedBlocks(configuration);
        plugin.getListener().getGriefConfig().loadConfig(configuration);
        sender.sendMessage("§f[§6WGGF§f] Конфиг был перезагружен!");
        return true;
    }
}
