package at.theduggy.world_manager;

import at.theduggy.BaseCommand;
import at.theduggy.Main;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WorldManager extends BaseCommand.CommandStructure {

    private final ArrayList<String> custom_worlds = new ArrayList<>();
    private final Path CUSTOM_WORLDS = new File(Main.getPlugin(Main.class).getDataFolder(), "/saves/").toPath();


    public WorldManager(String name, String prefix, FileConfiguration config) {
        super(name, prefix);
        loadWorlds();
    }

    private void loadWorlds(){
        for (File f : CUSTOM_WORLDS.toFile().listFiles()) {
            custom_worlds.add(f.getName());
            Bukkit.getServer().createWorldCustomStorage(new WorldCreator(f.getName()), f.toPath());
        }
    }

    private boolean worldLoaded(String name){
       return Bukkit.getWorlds().stream().filter(world -> name.equals(world.getName())).findFirst().orElse(null) != null;
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

                if (!custom_worlds.contains(world.getName())){
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
                        if (!worldLoaded(args[1])){
                            if (WorldType.getByName(args[2].toUpperCase()) != null){
                                WorldCreator worldCreator = new WorldCreator(args[1]);
                                worldCreator.type(WorldType.getByName(args[2].toUpperCase()));
                                Bukkit.getServer().createWorldCustomStorage(worldCreator, new File(Main.getPlugin(Main.class).getDataFolder(), "/saves/").toPath());
                                custom_worlds.add(args[1]);
                            } else {
                                sender.sendMessage(getPREFIX() + ChatColor.RED + "The world-type " + ChatColor.YELLOW + args[2] + ChatColor.RED + " doesn't exist!");
                            }
                        } else {
                            sender.sendMessage(getPREFIX() + ChatColor.RED + "The world " + ChatColor.YELLOW + args[1] + ChatColor.RED + " already exists!");
                        }
                    } else {
                        sender.sendMessage(getPREFIX() + ChatColor.RED + "Usage: /circinus world_manager create <name> <FLAT | AMPLIFIED | LARGEBIOMES | DEFAULT>");
                    }
                }

                case "teleport" -> {
                    if (args.length == 2) {
                        if (worldLoaded(args[1])){

                            Player player = (Player) sender;
                            if (!player.getWorld().getName().equals(args[1])){
                                player.teleport(Bukkit.getWorlds().stream().filter(world -> args[1].equals(world.getName())).findFirst().get().getSpawnLocation());
                            } else {
                                sender.sendMessage(getPREFIX() + ChatColor.RED + "You are already connected to this world!");
                            }
                        } else {
                            sender.sendMessage(getPREFIX() + ChatColor.RED + "The world " + ChatColor.YELLOW + args[1] + ChatColor.RED + " doesn't exist!");
                        }
                    }
                }

                default -> sender.sendMessage(getPREFIX() + ChatColor.YELLOW + args[0] + ChatColor.RED + " is not a valid argument!");
            }
        }
    }

    @Override
    public ArrayList<String> complete(String[] args, CommandSender sender) {
        System.out.println("Len: " + args.length + " Strings: " + Arrays.toString(args));
        if (args.length == 1){
            return new ArrayList<>(Arrays.asList("create", "teleport"));
        } else if (args.length > 0){
            switch (args[0]){
                case "create" -> {
                    if (args.length == 3){
                        return (ArrayList<String>) Arrays.stream(WorldType.values()).map(WorldType::getName).collect(Collectors.toList());
                    } else {
                        return new ArrayList<>();
                    }
                }

                case "teleport" -> {
                    if (args.length == 2){
                        return (ArrayList<String>) Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                    } else {
                        return new ArrayList<>();
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}
