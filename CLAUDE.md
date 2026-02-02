# Sign Commands - Development Guide

Spigot/Paper plugin that lets server admins assign commands to signs and blocks. Players click signs to trigger commands.

## Build

```bash
mvn clean package
```

Output: `target/SignCommands-<version>.jar`

Requires Java 21. Target API: Spigot 1.20.

## Project Structure

```
src/main/java/fun/sunrisemc/signcommands/
├── SignCommandsPlugin.java          # Plugin entry point, lifecycle, registration
├── command/
│   ├── SignCommandsCommand.java     # /signcommands command handler + tab completion
│   └── SignEditCommand.java         # /signedit command handler + tab completion
├── config/
│   └── MainConfig.java             # Loads config.yml settings
├── event/
│   ├── BlockBreak.java             # Protects command signs from breaking
│   ├── BlockInteract.java          # Detects clicks on command signs, triggers execution
│   ├── PlayerJoin.java             # Loads player data on join
│   └── SignChange.java             # Protects command signs from text edits
├── file/
│   ├── ConfigFile.java             # config.yml file manager
│   ├── DataFile.java               # signs.yml file manager
│   └── PlayerDataFile.java         # Per-player YAML files in playerdata/
├── hook/
│   └── Vault.java                  # Optional Vault economy/permissions integration
├── permission/
│   ├── Permissions.java            # All permission node constants
│   └── ProtectionCheck.java        # Block protection checks
├── scheduler/
│   ├── AutoSaveTask.java           # Periodic data saving
│   └── TickCounterTask.java        # Tick counter for click delay enforcement
├── sign/
│   ├── CommandSign.java            # Sign data model (commands, permissions, cooldowns, limits, costs)
│   ├── CommandSignManager.java     # Singleton registry: lookup by name, location, or ray-trace
│   └── command/
│       ├── SignClickType.java       # LEFT_CLICK, RIGHT_CLICK, ANY_CLICK
│       ├── SignCommand.java         # Individual command: click type + command type + command string
│       └── SignCommandType.java     # CONSOLE, PLAYER, OP, MESSAGE, BROADCAST
├── user/
│   ├── CommandSignUser.java        # Per-player sign interaction data (clicks, cooldowns)
│   └── CommandSignUserManager.java # Player data registry
└── utils/
    ├── Names.java                  # Enum display name conversion
    ├── PlayerUtils.java            # Ray-trace to find looked-at block/sign
    ├── SignUtils.java              # Sign type checking
    ├── StringUtils.java            # Parsing, time formatting, tab completion helpers
    └── YAMLUtils.java              # Safe YAML access, key migration
```

## Key Architecture

- **CommandSignManager** is a singleton that holds all signs in memory, indexed by ID, location, and name. Signs persist to `signs.yml` via DataFile.
- **CommandSignUserManager** holds per-player data in memory, persisted to individual YAML files in `playerdata/`.
- **BlockInteract** is the main interaction entry point. It checks sneak state, click delay, then delegates to `CommandSign.execute()`.
- **CommandSign.execute()** validates permissions, cooldowns, click limits, and economy cost before running each SignCommand.
- **Vault integration** is optional. The plugin checks for Vault on startup and only enables economy features if present.

## Key Conventions

- All permission strings are defined as constants in `Permissions.java`. These must match `plugin.yml`.
- `plugin.yml` version uses `${project.version}` — set the version in `pom.xml`.
- Enums (SignClickType, SignCommandType) use `Names.java` for display name conversion (e.g. `LEFT_CLICK` <-> `Left-Click`).
- Commands that operate on "the sign you are looking at" use `CommandSignManager.getLookingAt()` which ray-traces 64 blocks.
- Data migration logic lives in `SignCommandsPlugin.onEnable()` for version upgrades.

## Testing

No automated tests. Test manually on a Spigot or Paper server:

1. Build with `mvn clean package`
2. Copy the jar to the server's `plugins/` folder
3. Start/restart the server
4. Test sign creation, command execution, permissions, cooldowns, and economy features in-game
