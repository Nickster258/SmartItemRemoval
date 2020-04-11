package org.stonecipher;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class SmartItemRemoval extends JavaPlugin implements Listener {

    private FileConfiguration config = getConfig();
    private ArrayList<Item> items = new ArrayList<Item>();
    private int limit = 0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        limit = config.getInt("item_count_limit");
        getLogger().info("Set up max item count of " + limit);
        setupConfig();
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
            e.setCancelled(true);
            return;
        }
        if (items.size() > limit) {
            Item tmp = items.get(0);
            tmp.remove();
            items.remove(0);
        }
        items.add(e.getItemDrop());
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
