package at.theduggy;

import at.theduggy.world_manager.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static String BASE_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Circinus" + ChatColor.DARK_GRAY + "] ";
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        BaseCommand baseCommand = new BaseCommand();
        baseCommand.addCommand(new WorldManager("world_manager", ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "World-Manager" + ChatColor.DARK_GRAY + "] ", this.getConfig()));
        this.getCommand("circinus").setExecutor(baseCommand);
        this.getCommand("circinus").setTabCompleter(baseCommand);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}