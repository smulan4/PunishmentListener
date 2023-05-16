package se.neptunmc.punishmentlistener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

@Plugin(
        id = "punishment-listener",
        name = "PunishmentListener",
        version = BuildConstants.VERSION
)
public class PunishmentListener {

    private final ProxyServer proxyServer;
    private final ChannelRegistrar channelRegistrar;

    @Inject
    public PunishmentListener(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        this.channelRegistrar = proxyServer.getChannelRegistrar();
    }

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        channelRegistrar.register(MinecraftChannelIdentifier.create("punishment_namespace", "punish_channel"));
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().getId().equals("punishment_namespace:punish_channel")) {

            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

            short commandLength = in.readShort();
            byte[] commandBytes = new byte[commandLength];
            in.readFully(commandBytes);
            String command = new String(commandBytes);

            Player player = (Player) event.getTarget();

            proxyServer.getCommandManager().executeAsync(player, command);
        }
    }
}
