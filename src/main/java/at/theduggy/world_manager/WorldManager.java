package at.theduggy.world_manager;

import at.theduggy.BaseCommand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WorldManager extends BaseCommand.CommandStructure {

    private final HashMap<String, World> custom_worlds = new HashMap<>();
    private final FileConfiguration config;

    public WorldManager(String name, String prefix, FileConfiguration config) {
        super(name, prefix);
        this.config = config;
    }

    @Override
    public void run(String[] args, CommandSender sender) {
        System.out.println(Arrays.toString(args));
        if (args.length == 0){
            StringBuilder msg = new StringBuilder();
            msg.append(getPREFIX() + ChatColor.GRAY + "The Following worlds are loaded: ");
            int breaks = 1;
            for (int i = 0; i < Bukkit.getWorlds().size(); i++){
                World world = Bukkit.getWorlds().get(i);
                StringBuilder world_name = new StringBuilder();

                world_name.append(ChatColor.GREEN + world.getName());

                if (!custom_worlds.containsKey(world.getName())){
                    world_name.append(ChatColor.AQUA + " (DEFAULT)" + ChatColor.GRAY);
                }

                if (i < Bukkit.getWorlds().size() - 1){
                    world_name.append(", ");
                }
                if (msg.length() + world_name.length() > 53 * breaks){
                    breaks++;
                    msg.append("\n" + " ".repeat(getPREFIX().length()));
                }
                msg.append(world_name);
            }
            sender.sendMessage(msg.toString());
        } else {
            switch (args[0]){
                case "create" -> {
                    if (args.length == 3) {
                        if (Bukkit.getWorlds().stream().filter(world -> args[1].equals(world.getName())).findFirst().orElse(null) == null){
                            if (WorldType.getByName(args[2].toUpperCase()) != null){
                                WorldCreator worldCreator = new WorldCreator(args[2]);
                                worldCreator.type(WorldType.getByName(args[2].toUpperCase()));
                                worldCreator.createWorld();
                                config.set("worlds." + args[2], "");

                            } else {
                                sender.sendMessage(getPREFIX() + ChatColor.RED + "The world-type " + ChatColor.YELLOW + " doesn't exist!");
                            }
                        } else {
                            sender.sendMessage(getPREFIX() + ChatColor.RED + "The world " + ChatColor.YELLOW + args[1] + ChatColor.RED + " already exists!");
                        }
                    }
                }

                default -> sender.sendMessage(getPREFIX() + ChatColor.YELLOW + args[0] + ChatColor.RED + " is not a valid argument!");
            }
        }
    }

    @Override
    public ArrayList<String> complete(String[] args, CommandSender sender) {
        if (args.length <= 1){
            return new ArrayList<>(Arrays.asList("create"));
        } else {
            switch (args[0]){
                case "create" -> {
                    if (args.length == 2){
                        return (ArrayList<String>) Arrays.stream(WorldType.values()).map(WorldType::getName).collect(Collectors.toList());
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}
