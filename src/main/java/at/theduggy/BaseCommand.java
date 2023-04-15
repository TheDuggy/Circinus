package at.theduggy;

import at.theduggy.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BaseCommand implements TabExecutor {

    private final HashMap<String, CommandStructure> commands = new HashMap<>();

    public void addCommand(CommandStructure cmd){
        this.commands.put(cmd.getName(), cmd);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commands.containsKey(strings[0])){
            commands.get(strings[0]).run(Arrays.copyOfRange(strings, 1, strings.length), commandSender);
        } else {
            commandSender.sendMessage(Main.BASE_PREFIX + ChatColor.RED + "The command " + ChatColor.YELLOW + strings[0] + ChatColor.RED + " doesn't exist!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        System.out.println("Base: " + Arrays.toString(strings));
        if (strings.length == 1){
            return new ArrayList<>(commands.keySet());
        } else if (commands.containsKey(strings[0])){
            return commands.get(strings[0]).complete(Arrays.copyOfRange(strings, 1, strings.length), commandSender);
        }
        return new ArrayList<>();
    }

    public static abstract class CommandStructure {
        private final String name;
        private final String PREFIX;
        public CommandStructure(String name, String prefix){
            this.name = name;
            this.PREFIX = prefix;
        }
        public abstract void run(String args[], CommandSender sender);
        public abstract ArrayList<String> complete(String args[], CommandSender sender);

        public String getName(){
            return name;
        }

        public String getPREFIX() {
            return PREFIX;
        }
    }


}
