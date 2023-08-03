package fr.wakleg.ecobric;

import fr.wakleg.market.command.MarketCommand;
import fr.wakleg.ecobric.command.MoneyCommand;
import fr.wakleg.ecobric.command.PayCommand;
import fr.wakleg.ecobric.command.WithdrawCommand;
import fr.wakleg.ecobric.event.ModPlayerEventCopyFrom;
import fr.wakleg.ecobric.item.ModItems;
import fr.wakleg.market.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static String MOD_ID = "ecobric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mod " + MOD_ID);

		CommandRegistrationCallback.EVENT.register(MoneyCommand::register);
		CommandRegistrationCallback.EVENT.register(WithdrawCommand::register);
		CommandRegistrationCallback.EVENT.register(PayCommand::register);

		ServerPlayerEvents.COPY_FROM.register(new ModPlayerEventCopyFrom());

		ModItems.registerModItems();
	}
}