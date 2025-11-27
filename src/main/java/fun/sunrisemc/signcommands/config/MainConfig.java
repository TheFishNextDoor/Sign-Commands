package fun.sunrisemc.signcommands.config;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.signcommands.SignCommandsPlugin;
import fun.sunrisemc.signcommands.file.ConfigFile;
import fun.sunrisemc.signcommands.utils.YAMLUtils;

public class MainConfig {

    public final boolean ONLY_ALLOW_SIGNS; // If true, only allow signs to have commands assigned to them. If false, any block can have commands assigned.

    public final int SIGN_CLICK_DELAY_TICKS; // The number of ticks a player must wait between running sign commands.

    public MainConfig() {
        SignCommandsPlugin.logInfo("Loading main config...");

        YamlConfiguration config = ConfigFile.get("config", true);

        this.ONLY_ALLOW_SIGNS = YAMLUtils.getBoolean(config, "only-allow-signs").orElse(true);
        this.SIGN_CLICK_DELAY_TICKS = YAMLUtils.getIntClamped(config, "sign-click-delay-ticks", 0, Integer.MAX_VALUE).orElse(5);

        SignCommandsPlugin.logInfo("Main config loaded.");
    }
}