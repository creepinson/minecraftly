package com.minecraftly.bukkit.listeners;

import com.ikeirnez.pluginmessageframework.packet.PacketHandler;
import com.minecraftly.bukkit.utilities.BukkitUtilities;
import com.minecraftly.packets.LocationContainer;
import com.minecraftly.packets.PacketTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

/**
 * Handles any core incoming packets.
 */
public class PacketListener {

    @PacketHandler
    public void onPacketTeleport(Player player, PacketTeleport packetTeleport) {
        Location location;
        UUID playerUUID = packetTeleport.getPlayerUUID();
        LocationContainer locationContainer = packetTeleport.getLocationContainer();

        if (playerUUID != null) {
            Player target = Bukkit.getPlayer(playerUUID);
            if (target != null) {
                location = target.getLocation();
            } else {
                throw new IllegalArgumentException("Invalid teleport location, player '" + playerUUID + "' is not connected to this instance.");
            }
        } else if (locationContainer != null) {
            location = BukkitUtilities.getLocation(locationContainer);

            if (location.getWorld() == null) {
                throw new IllegalArgumentException("Invalid teleport location, world '" + locationContainer.getWorld() + "' doesn't exist.");
            }
        } else {
            throw new UnsupportedOperationException("Don't know how to handle a teleport packet with all null parameters.");
        }

        if (location != null) {
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            throw new UnsupportedOperationException("Attempted teleport to null location.");
        }
    }

}
