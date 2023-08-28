package fr.wakleg.market;

import fr.wakleg.market.command.MarketCommand;
import fr.wakleg.market.command.SellCommand;
import fr.wakleg.market.event.LoginHandler;
import fr.wakleg.market.screen.handler.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(MarketCommand::register);
        CommandRegistrationCallback.EVENT.register(SellCommand::register);

        ServerEntityEvents.ENTITY_LOAD.register(new LoginHandler());

        ModScreenHandlers.registerAllScreenHandlers();
    }
}
