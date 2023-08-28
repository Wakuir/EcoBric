package fr.wakleg.market.screen.handler;

import fr.wakleg.ecobric.Main;
import fr.wakleg.market.screen.handler.ConfirmScreenHandler;
import fr.wakleg.market.screen.handler.MarketScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<MarketScreenHandler> MARKET_SCREEN_HANDLER;
    public static ScreenHandlerType<ConfirmScreenHandler> CONFIRM_SCREEN_HANDLER;
    public static ScreenHandlerType<OwnedItemsScreenHandler> OWNED_ITEMS_SCREEN_HANDLER;

    public static void registerAllScreenHandlers(){
        MARKET_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Main.MOD_ID, "market_screen_handler"), MarketScreenHandler::new);/*new ScreenHandlerType<>(MarketScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);*/
        CONFIRM_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Main.MOD_ID, "confirm_screen_handler"), ConfirmScreenHandler::new);
        OWNED_ITEMS_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Main.MOD_ID, "owned_items_screen_handler"), OwnedItemsScreenHandler::new);
    }
}