package ru.overwrite.wggf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.plugin.java.annotation.plugin.author.Authors;
import ru.overwrite.wggf.listeners.PlayerListener;

@Plugin(name = "WorldGuardGriefFix", version = "3.0.0")
@Website("OverwriteMC*975*#3748, t.me/afekvova")
@Authors({@Author("Overwrite"), @Author("Afek")})
@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "wggf", aliases = {"worldguardgrieffix"}))
public class WorldGuardGriefFixPlugin extends JavaPlugin {

    private PlayerListener listener;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.listener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(this.listener, this);
        this.getCommand("wggf").setExecutor(this);

        // We need Metrics support!
        new Metrics(this, 14247);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("wggf.command.reload")) {
            sender.sendMessage(this.color("&cYou don't have permission!"));
            return true;
        }

        if (args.length <= 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(this.color("&f[&6WGGF&f] /wggf reload - перезагрузить плагин"));
            return true;
        }

        this.reloadConfig();
        this.listener.loadProtectedRegion(this.getConfig());
        sender.sendMessage(this.color("&f[&6WGGF&f] Вы успешно перезагрузили плагин!"));
        return true;
    }

    private String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}

