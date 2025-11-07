package fun.sunrisemc.sign_commands.config;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.sign_commands.file.ConfigFile;
import fun.sunrisemc.sign_commands.file.YAMLUtils;

public class MainConfig {

    public final boolean ONLY_ALLOW_SIGNS; // If true, only allow signs to have commands assigned to them. If false, any block can have commands assigned.

    public final int SIGN_CLICK_COOLDOWN_TICKS; // The number of ticks a player must wait between running sign commands.

    public MainConfig() {
        YamlConfiguration config = ConfigFile.get("config", true);

        this.ONLY_ALLOW_SIGNS = config.getBoolean("only-allow-signs", true);
        this.SIGN_CLICK_COOLDOWN_TICKS = YAMLUtils.getIntClamped(config, "sign-click-cooldown-ticks", 0, Integer.MAX_VALUE);
    }
}