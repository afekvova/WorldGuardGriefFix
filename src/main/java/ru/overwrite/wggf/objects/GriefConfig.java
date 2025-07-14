package ru.overwrite.wggf.objects;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class GriefConfig {
    private boolean enablePistons;
    private boolean enableMinecart;
    private boolean enableWither;
    private boolean enableWitherSkull;
    private boolean enableAnyExplosion;
    private boolean enableFallingBlock;
    private boolean enableFishing;

    public void loadConfig(FileConfiguration configuration) {
        this.enablePistons = configuration.getBoolean("enable-pistons", true);
        this.enableMinecart = configuration.getBoolean("enable-minecart", true);
        this.enableWither = configuration.getBoolean("enable-wither", true);
        this.enableWitherSkull = configuration.getBoolean("enable-wither-skull", true);
        this.enableAnyExplosion = configuration.getBoolean("enable-any-explosion", true);
        this.enableFallingBlock = configuration.getBoolean("enable-falling-block", true);
        this.enableFishing = configuration.getBoolean("enable-fishing", true);
    }
}
