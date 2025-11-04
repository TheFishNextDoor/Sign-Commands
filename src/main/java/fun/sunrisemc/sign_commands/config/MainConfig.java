package fun.sunrisemc.sign_commands.config;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.sign_commands.file.ConfigFile;

public class MainConfig {

    public final boolean ONLY_ALLOW_SIGNS;

    public final int SIGN_CLICK_COOLDOWN_TICKS;

    public MainConfig() {
        YamlConfiguration config = ConfigFile.get("config", true);

        this.ONLY_ALLOW_SIGNS = config.getBoolean("only-allow-signs", true);
        this.SIGN_CLICK_COOLDOWN_TICKS = ConfigFile.getIntClamped(config, "sign-click-cooldown-ticks", 0, Integer.MAX_VALUE);
    }
}