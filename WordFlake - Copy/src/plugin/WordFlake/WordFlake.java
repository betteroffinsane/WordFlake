package plugin.WordFlake;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class WordFlake extends JavaPlugin implements Listener {

    public static final String REPLACE_WORDS_YML_PREFIX = "replace-words.";
    public static final String REPLACE_WORDS_YML_SECTION = "replace-words";
    private Map<String, String> swearWordMappings;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        Set<String> swearWords = getSwearWordsOnly();
        swearWordMappings = getMappings(swearWords, getConfig());
        // All you have to do is adding this line in your onEnable method:
        plugin.WordFlake.Metrics metrics = new plugin.WordFlake.Metrics(this);
        // Optional: Add custom charts
        metrics.addCustomChart(new plugin.WordFlake.Metrics.SimplePie("chart_id", () -> "My value"));
    }

    private Map<String, String> getMappings(Set<String> swearWords, FileConfiguration config) {
        return new HashMap<>(swearWords
                .stream()
                .collect(toMap(Function.identity(), s -> config.getString(REPLACE_WORDS_YML_PREFIX + s))));
    }

    private Set<String> getSwearWordsOnly() {
        return getConfig().getConfigurationSection(REPLACE_WORDS_YML_SECTION).getKeys(false);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String originalMessage = event.getMessage();
        String message = getFilteredMessage(originalMessage, swearWordMappings);
        event.setMessage(message);
    }

    static String getFilteredMessage(String originalMessage, Map<String, String> swearWordMappings) {
        for (String swearWord : swearWordMappings.keySet()) {
                swearWordMappings.get(swearWord);
                originalMessage = originalMessage.replace(swearWord, swearWordMappings.get(swearWord));
        }
        return originalMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wordr") && sender.hasPermission("wordflake.perms")) {
            sender.sendMessage(ChatColor.GREEN + "[WordFlake] Reloaded");
            saveDefaultConfig();
            reloadConfig();
            swearWordMappings = getMappings(getSwearWordsOnly(), getConfig());
        }
        if (label.equalsIgnoreCase("wordadd") && sender.hasPermission("wordflake.perms")) {
            if (args.length == 0 || args.length > 2) {
                sender.sendMessage(ChatColor.RED + "[WordFlake] Usage: /wordadd (what to replace with)");
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "[WordFlake] Usage: /wordadd (word to add) (what to replace with)");
            }
            if (args.length == 2) {
                String keyadd = args[0];
                String stringadd = args[1];
                getConfig().set(REPLACE_WORDS_YML_PREFIX + keyadd, stringadd);
                sender.sendMessage(ChatColor.GREEN + "[WordFlake] You have successfully added a word to the list.");
                saveConfig();
                swearWordMappings.put(keyadd, stringadd);
            }
        }
        return true;
    }

}