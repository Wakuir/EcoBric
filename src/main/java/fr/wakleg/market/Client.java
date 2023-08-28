package fr.wakleg.market;

import fr.wakleg.market.screen.MarketScreen;
import fr.wakleg.market.screen.OwnedItemsScreen;
import fr.wakleg.market.screen.handler.ModScreenHandlers;
import fr.wakleg.market.screen.ConfirmScreen;
import fr.wakleg.market.screen.handler.OwnedItemsScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.MARKET_SCREEN_HANDLER, MarketScreen::new);
        HandledScreens.register(ModScreenHandlers.CONFIRM_SCREEN_HANDLER, ConfirmScreen::new);
        HandledScreens.register(ModScreenHandlers.OWNED_ITEMS_SCREEN_HANDLER, OwnedItemsScreen::new);
    }
}