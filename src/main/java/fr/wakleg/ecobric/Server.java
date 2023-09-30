package fr.wakleg.ecobric;

import fr.wakleg.Saver;
import fr.wakleg.ecobric.command.MoneyCommand;
import fr.wakleg.ecobric.command.PayCommand;
import fr.wakleg.ecobric.command.WithdrawCommand;
import fr.wakleg.ecobric.event.ModPlayerEventCopyFrom;
import fr.wakleg.market.command.MarketCommand;
import fr.wakleg.market.command.SellCommand;
import fr.wakleg.market.event.LoginHandler;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class Server implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register(MoneyCommand::register);
        CommandRegistrationCallback.EVENT.register(WithdrawCommand::register);
        CommandRegistrationCallback.EVENT.register(PayCommand::register);

        ServerPlayerEvents.COPY_FROM.register(new ModPlayerEventCopyFrom());

        ServerEntityEvents.ENTITY_LOAD.register(new LoginHandler());
    }
}
