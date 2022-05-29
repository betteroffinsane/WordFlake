package plugin.WordFlake;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

public class WordFlake extends JavaPlugin implements Listener {

    public static final String REPLACE_WORDS_YML_PREFIX = "replace-words.";
    public static final String REPLACE_WORDS_YML_SECTION = "replace-words";
    private Map<SwearWordPattern, String> swearWordMappings;

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

    private Map<SwearWordPattern, String> getMappings(Set<String> swearWords, FileConfiguration config) {
        return new HashMap<>(swearWords
                .stream()
                .collect(toMap(WordFlake::compileRegexPattern, s -> config.getString(REPLACE_WORDS_YML_PREFIX + s))));
    }

    static SwearWordPattern compileRegexPattern(String swearWord) {
        StringBuilder generalStringBuilder = new StringBuilder(".*");
        StringBuilder swearWordPatternStringBuilder = new StringBuilder("(?i)(");
        for (int i = 0; i < swearWord.length() - 1; i++) {
            swearWordPatternStringBuilder.append(swearWord.charAt(i)).append("+\\s*");
        }
        swearWordPatternStringBuilder.append(swearWord.charAt(swearWord.length() - 1)).append("+").append(")");
        generalStringBuilder.append(swearWordPatternStringBuilder).append(".*");
        return new SwearWordPattern(swearWord,
                Pattern.compile(swearWordPatternStringBuilder.toString()),
                Pattern.compile(generalStringBuilder.toString()));
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

    static String getFilteredMessage(String originalMessage, Map<SwearWordPattern, String> swearWordMappings) {
        for (SwearWordPattern pattern : swearWordMappings.keySet()) {
            while (originalMessage.matches(pattern.fullMessageRegex().pattern())) {
                originalMessage = originalMessage.replaceFirst(pattern.swearWordRegex().pattern(), swearWordMappings.get(pattern));
            }

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
                swearWordMappings.put(compileRegexPattern(keyadd), stringadd);
            }
        }
        return true;
    }

    record SwearWordPattern(String swearWord, Pattern swearWordRegex, Pattern fullMessageRegex) {
    }

}