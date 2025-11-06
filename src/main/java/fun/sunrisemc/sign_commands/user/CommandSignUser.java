package fun.sunrisemc.sign_commands.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.file.DataFile;
import fun.sunrisemc.sign_commands.utils.StringUtils;

public class CommandSignUser {

    private final UUID uuid;

    private HashMap<String, Long> lastSignClickMap = new HashMap<>();

    private HashMap<String, Integer> signClicksMap = new HashMap<>();

    public CommandSignUser(UUID uuid) {
        this.uuid = uuid;

        String id = uuid.toString();
        YamlConfiguration playerData = DataFile.get(id);

        for (String signClicksString : playerData.getStringList(".sign-clicks")) {
            String[] parts = signClicksString.split(":");

            String signId = parts[0];
            String clicksString = parts[1];
            
            Optional<Integer> clicks = StringUtils.parseInteger(clicksString);
            if (clicks.isPresent()) {
                signClicksMap.put(signId, clicks.get());
            }
        }

        for (String lastSignClickString : playerData.getStringList(".last-sign-click")) {
            String[] parts = lastSignClickString.split(":");

            String signId = parts[0];
            String timestampString = parts[1];
            
            Optional<Long> timestamp = StringUtils.parseLong(timestampString);
            if (timestamp.isPresent()) {
                lastSignClickMap.put(signId, timestamp.get());
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public long getLastSignClickTimeMillis(CommandSign commandSign) {
        String name = commandSign.getName();
        return lastSignClickMap.getOrDefault(name, 0L);
    }

    public void resetLastSignClickTimeMillis(CommandSign commandSign) {
        String name = commandSign.getName();
        lastSignClickMap.remove(name);
    }
    
    public int getTotalSignClicks(CommandSign commandSign) {
        String name = commandSign.getName();
        long lastReset = commandSign.getLastUserClickLimitResetTimeMillis();
        if (lastReset > getLastSignClickTimeMillis(commandSign)) {
            signClicksMap.remove(name);
            return 0;
        }
        return signClicksMap.getOrDefault(name, 0);
    }

    public void resetTotalSignClicks(CommandSign commandSign) {
        String name = commandSign.getName();
        signClicksMap.remove(name);
    }

    public void onSignExecute(CommandSign commandSign) {
        String name = commandSign.getName();
        long currentTimeMillis = System.currentTimeMillis();
        int totalSignClicks = getTotalSignClicks(commandSign);
        signClicksMap.put(name, totalSignClicks + 1);
        lastSignClickMap.put(name, currentTimeMillis);
    }

    public void save() {
        YamlConfiguration playerData = getPlayerDataFile();

        ArrayList<String> signClicksList = new ArrayList<>();
        for (Entry<String, Integer> entry : signClicksMap.entrySet()) {
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

        savePlayerDataFile(playerData);

        if (!isOnline()) {
            CommandSignUserManager.unload(uuid);
        }
    }

    private YamlConfiguration getPlayerDataFile() {
        String id = uuid.toString();
        return DataFile.get(id);
    }

    private void savePlayerDataFile(YamlConfiguration playerData) {
        String id = uuid.toString();
        DataFile.save(id, playerData);
    }
}