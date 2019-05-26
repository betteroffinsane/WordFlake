package plugin.WordFlake;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;

public class WordFlake extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        // All you have to do is adding this line in your onEnable method:
        plugin.WordFlake.Metrics metrics = new plugin.WordFlake.Metrics(this);
        // Optional: Add custom charts
        metrics.addCustomChart(new plugin.WordFlake.Metrics.SimplePie("chart_id", () -> "My value"));
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        String originalMessage = event.getMessage();
        ArrayList<String> swearWords = new ArrayList<>();
        for (String key : getConfig().getConfigurationSection("replace-words").getKeys(false))
            swearWords.add(key);
        ArrayList<Character> charArrayList = new ArrayList<>();
        for (char c : originalMessage.toCharArray())
            charArrayList.add(c);
        for (String swearWord : swearWords)
        {
            int startIndex;
            if ((startIndex = originalMessage.toLowerCase().indexOf(swearWord)) != -1)
            {
                int endIndex = startIndex + swearWord.length();
                for (int i = startIndex; i < endIndex; i++) {
                    if (charArrayList.size() <= 1) {
                        charArrayList.clear();
                    } else
                    {
                        charArrayList.remove(startIndex);
                        charArrayList.trimToSize();
                    }
                }
                char[] replaceWith = getConfig().getString("replace-words." + swearWord).toCharArray();
                for (int i = 0; i < replaceWith.length; i++)
                {
                    char c = replaceWith[i];
                    charArrayList.add(startIndex + i, c);
                }
                break;
            }
        }
        final char[] array = new char[charArrayList.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = charArrayList.get(i);
        event.setMessage(new String(array));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (label.equalsIgnoreCase("wordr") && sender.hasPermission("wordflake.perms"))
        {
            sender.sendMessage(ChatColor.GREEN + "[WordFlake] Reloaded");
            saveDefaultConfig();
            reloadConfig();
        }
        if (label.equalsIgnoreCase("wordadd") && sender.hasPermission("wordflake.perms")) {
            if (args.length == 0 || args.length > 2)
            {
                sender.sendMessage(ChatColor.RED + "[WordFlake] Usage: /wordadd (what to replace with)");
            }
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "[WordFlake] Usage: /wordadd (word to add) (what to replace with)");
                }
                    if (args.length == 2)
                    {
                        String keyadd = args[0];
                        String stringadd = args[1];
                        getConfig().set("replace-words." + keyadd, stringadd);
                        sender.sendMessage(ChatColor.GREEN + "[WordFlake] You have successfully added a word to the list.");
                        saveConfig();
                    }
                }
                return true;
    }

}



















