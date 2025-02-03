package com.TpoToggle; 


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.Set;

public class TpoToggle extends JavaPlugin implements Listener {


    private Set<String> tpoEnabledPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("TpoToggle enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TpoToggle disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("tpotoggle.use")) {
                if (tpoEnabledPlayers.contains(player.getName())) {

                    tpoEnabledPlayers.remove(player.getName());
                    sendConfigMessage(player, "tpo_off");
                } else {

                    tpoEnabledPlayers.add(player.getName());
                    sendConfigMessage(player, "tpo_on");
                }
            } else {
                player.sendMessage("You do not have permission to use this command.");
            }
        } else {
            sender.sendMessage("Only players can use this command.");
        }
        return true;
    }

    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player teleporter = event.getPlayer(); 
        Location destination = event.getTo(); 

        for (Player target : getServer().getOnlinePlayers()) {
            if (target.getLocation().distance(destination) < 1) { 
                if (tpoEnabledPlayers.contains(target.getName())) {
                    if (!teleporter.hasPermission("tpotoggle.bypass")) {
                        event.setCancelled(true); 
                        teleporter.sendMessage("Teleport protection is enabled for this player.");
                    }
                }
                break;
            }
        }
    }


    private void sendConfigMessage(Player player, String configKey) {
        FileConfiguration config = getConfig();
        String message = config.getString("messages." + configKey);
        if (message != null) {
            player.sendMessage(message);
        } else {
            player.sendMessage("Error: Message not found in config.");
        }
    }
}
