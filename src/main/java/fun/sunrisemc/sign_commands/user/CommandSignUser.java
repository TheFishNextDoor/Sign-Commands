package fun.sunrisemc.sign_commands.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fun.sunrisemc.sign_commands.file.DataFile;
import fun.sunrisemc.sign_commands.utils.StringUtils;

public class CommandSignUser {

    private final UUID uuid;

    private HashMap<String, Integer> signClicks = new HashMap<>();

    private HashMap<String, Long> lastSignClick = new HashMap<>();

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
                signClicks.put(signId, clicks.get());
            }
        }

        for (String lastSignClickString : playerData.getStringList(".last-sign-click")) {
            String[] parts = lastSignClickString.split(":");

            String signId = parts[0];
            String timestampString = parts[1];
            
            Optional<Long> timestamp = StringUtils.parseLong(timestampString);
            if (timestamp.isPresent()) {
                lastSignClick.put(signId, timestamp.get());
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public boolean isOnline() {
        return getPlayer().isPresent();
    }

    public void save() {
        String id = uuid.toString();
        YamlConfiguration playerData = DataFile.get(id);

        ArrayList<String> signClicksList = new ArrayList<>();
        for (Entry<String, Integer> entry : signClicks.entrySet()) {
            String signId = entry.getKey();
            Integer clicks = entry.getValue();
            signClicksList.add(signId + ":" + clicks);
        }
        playerData.set(".sign-clicks", signClicksList);

        ArrayList<String> lastSignClickList = new ArrayList<>();
        for (Entry<String, Long> entry : lastSignClick.entrySet()) {
            String signId = entry.getKey();
            Long timestamp = entry.getValue();
            lastSignClickList.add(signId + ":" + timestamp);
        }
        playerData.set(".last-sign-click", lastSignClickList);

        DataFile.save(id, playerData);

        if (!isOnline()) {
            CommandSignUserManager.unload(uuid);
        }
    }
    
    public int getSignClicks(String signId) {
        return signClicks.getOrDefault(signId, 0);
    }

    public long getLastSignClick(String signId) {
        return lastSignClick.getOrDefault(signId, 0L);
    }

    public void onSignClick(String signId) {
        int currentClicks = signClicks.getOrDefault(signId, 0);
        signClicks.put(signId, currentClicks + 1);
        lastSignClick.put(signId, System.currentTimeMillis());
    }
}