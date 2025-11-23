package fun.sunrisemc.signcommands.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.file.PlayerDataFile;
import fun.sunrisemc.signcommands.sign.CommandSign;
import fun.sunrisemc.signcommands.utils.StringUtils;

public class CommandSignUser {

    // UUID

    private final @NotNull UUID uuid;

    // Last Sign Click Time

    private @NotNull HashMap<String, Long> lastSignClickMap = new HashMap<>();

    // Sign Click Count

    private @NotNull HashMap<String, Integer> signClickCountMap = new HashMap<>();

    // Constructors

    public CommandSignUser(@NotNull UUID uuid) {
        this.uuid = uuid;
        load();
    }

    // UUID

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    private boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    // On Sign Execute

    public void onSignExecute(@NotNull CommandSign commandSign) {
        String name = commandSign.getName();
        long currentTimeMillis = System.currentTimeMillis();
        int totalSignClicks = getSignClickCount(commandSign);
        signClickCountMap.put(name, totalSignClicks + 1);
        lastSignClickMap.put(name, currentTimeMillis);
    }

    // Last Sign Click Time

    public long getLastSignClickTimeMillis(@NotNull CommandSign commandSign) {
        String name = commandSign.getName();
        return lastSignClickMap.getOrDefault(name, 0L);
    }

    public void resetLastSignClickTimeMillis(@NotNull CommandSign commandSign) {
        String name = commandSign.getName();
        lastSignClickMap.remove(name);
    }

    // Sign Click Count

    public int getSignClickCount(@NotNull CommandSign commandSign) {
        String name = commandSign.getName();
        long lastReset = commandSign.getLastUserClickLimitResetTimeMillis();
        if (lastReset > getLastSignClickTimeMillis(commandSign)) {
            signClickCountMap.remove(name);
            return 0;
        }
        return signClickCountMap.getOrDefault(name, 0);
    }

    public void resetSignClickCount(@NotNull CommandSign commandSign) {
        String name = commandSign.getName();
        signClickCountMap.remove(name);
    }

    // Loading and Saving

    protected void load() {
        YamlConfiguration playerData = PlayerDataFile.get(uuid);

        for (String signClicksString : playerData.getStringList(".sign-clicks")) {
            String[] parts = signClicksString.split(":");
            if (parts.length != 2) {
                continue;
            }

            String signId = parts[0];
            String clicksString = parts[1];
            
            Optional<Integer> clicks = StringUtils.parseInteger(clicksString);
            if (clicks.isPresent()) {
                signClickCountMap.put(signId, clicks.get());
            }
        }

        for (String lastSignClickString : playerData.getStringList(".last-sign-click")) {
            String[] parts = lastSignClickString.split(":");
            if (parts.length != 2) {
                continue;
            }

            String signId = parts[0];
            String timestampString = parts[1];
            
            Optional<Long> timestamp = StringUtils.parseLong(timestampString);
            if (timestamp.isPresent()) {
                lastSignClickMap.put(signId, timestamp.get());
            }
        }
    }

    protected void save() {
        YamlConfiguration playerData = PlayerDataFile.get(uuid);

        ArrayList<String> signClicksList = new ArrayList<>();
        for (Entry<String, Integer> entry : signClickCountMap.entrySet()) {
            String signId = entry.getKey();
            Integer clicks = entry.getValue();
            signClicksList.add(signId + ":" + clicks);
        }
        playerData.set(".sign-clicks", signClicksList);

        ArrayList<String> lastSignClickList = new ArrayList<>();
        for (Entry<String, Long> entry : lastSignClickMap.entrySet()) {
            String signId = entry.getKey();
            Long timestamp = entry.getValue();
            lastSignClickList.add(signId + ":" + timestamp);
        }
        playerData.set(".last-sign-click", lastSignClickList);

        PlayerDataFile.save(uuid, playerData);

        if (!isOnline()) {
            CommandSignUserManager.unload(uuid);
        }
    }
}