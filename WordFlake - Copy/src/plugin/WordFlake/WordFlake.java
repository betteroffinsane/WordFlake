package plugin.WordFlake;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class WordFlake extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

    }

    private HashMap<String, String> chatMap = new HashMap<>();


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        ConfigurationSection section = getConfig().getConfigurationSection("replace-words");
        for (String key : section.getKeys(false))
        {
            chatMap.put(key, section.getString(key));
            if (chatMap.containsKey(key)) {
                event.setMessage(event.getMessage().replace(key, section.getString((key))));

            }


        }

    }

    }
