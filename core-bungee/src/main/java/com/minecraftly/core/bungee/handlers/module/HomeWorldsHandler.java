package com.minecraftly.core.bungee.handlers.module;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.minecraftly.core.bungee.MinecraftlyBungeeCore;
import com.minecraftly.core.packets.homes.PacketPlayerGotoHome;
import com.sk89q.intake.Command;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Keir on 29/04/2015.
 */
public class HomeWorldsHandler implements Listener {

    private final MinecraftlyBungeeCore minecraftlyBungeeCore;
    private final RedisBungeeAPI redisBungeeAPI;

    // todo use redis
    private final Map<UUID, ServerInfo> worldServerMap = new HashMap<>();

    public HomeWorldsHandler(MinecraftlyBungeeCore minecraftlyBungeeCore) {
        this.minecraftlyBungeeCore = minecraftlyBungeeCore;
        this.redisBungeeAPI = this.minecraftlyBungeeCore.getRedisBungeeAPI();
    }

    @Command(aliases = "home", desc = "Teleport's the sender to their world")
    public void connectBestServer(ProxiedPlayer proxiedPlayer) {
        connectBestServer(proxiedPlayer, proxiedPlayer.getUniqueId());
    }

    public void connectBestServer(ProxiedPlayer proxiedPlayer, final UUID ownerUUID) {
        ServerInfo serverInfo = getServerHostingWorld(ownerUUID);
        if (serverInfo == null) {
            serverInfo = proxiedPlayer.getServer().getInfo();
        }

        playerGotoHome(serverInfo, proxiedPlayer, ownerUUID);
    }

    public void playerGotoHome(ServerInfo serverInfo, ProxiedPlayer proxiedPlayer, UUID ownerUUID) {
        ServerInfo hostingServer = worldServerMap.get(ownerUUID);
        if (hostingServer != null && !serverInfo.equals(hostingServer)) {
            throw new UnsupportedOperationException("Attempted to host a home on 2 different instances.");
        }

        minecraftlyBungeeCore.getGateway().sendPacket(proxiedPlayer, new PacketPlayerGotoHome(proxiedPlayer.getUniqueId(), ownerUUID));
    }

    public ServerInfo getServerHostingWorld(UUID worldUUID) {
        // todo check which server is hosting world remember to check for redis servers too
        return null;
    }

    // for cases whereby the server disconnecting from has no players online
    // to notify the proxy that it is no longer hosting a world
    // so we'll assume no worlds are being hosted
    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent e) {
        ServerInfo serverLeaving = e.getTarget();
        if (serverLeaving.getPlayers().size() == 0) {
            worldServerMap.values().removeIf(s -> s.equals(serverLeaving));
        }
    }

    /*@PacketHandler todo
    public void onNoLongerHosting(ProxiedPlayer proxiedPlayer, PacketNoLongerHosting packet) {
        playerServerMap.get(proxiedPlayer.getServer().getInfo()).remove(packet.getWorldUUID());
    }*/

}
