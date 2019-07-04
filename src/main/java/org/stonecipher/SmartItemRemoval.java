package org.stonecipher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class SmartItemRemoval extends JavaPlugin implements Listener {

    private ArrayList<Player> keepDropPlayers = new ArrayList<Player>();
    private FileConfiguration config = getConfig();
    private ArrayList<Item> items = new ArrayList<Item>();
    private int limit = 0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        setupConfig();
        limit = config.getInt("item_count_limit");
        getLogger().info("Set up max item count of " + limit);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if(cmd.getName().equalsIgnoreCase("keepdrops")) {
            if(keepDropPlayers.contains(sender)) {
                keepDropPlayers.remove(sender);
                sendMessage(sender, "You have opted out of retaining dropped items.");
            } else {
                keepDropPlayers.add((Player) sender);
                sendMessage(sender, "You are opted into retaining dropped items.");
            }
        }
        return true;
    }

    @EventHandler
    private void onItemSpawnEvent(ItemSpawnEvent e) {
        if (limit == 0) {
            e.setCancelled(true);
            return;
        }
        if (items.size() > limit) {
            Item tmp = items.get(0);
            tmp.remove();
            items.remove(0);
        }
        items.add(e.getEntity());
    }

    @EventHandler
    private void onPlayerItemDropEvent(PlayerDropItemEvent e) {
        if (limit == 0) {
            if (keepDropPlayers.contains(e.getPlayer())) {
                e.setCancelled(true);
            } else {
                e.getItemDrop().remove();
            }
            return;
        }
        if (items.size() > limit) {
            Item tmp = items.get(0);
            tmp.remove();
            items.remove(0);
        }
        items.add(e.getItemDrop());
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage("§7" + message);
    }

    private void setupConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            File file = new File(getDataFolder(), "config.yml");

            config.addDefault("item_count_limit", 0);
            config.options().copyDefaults(true);
            saveConfig();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
