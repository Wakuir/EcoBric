package fr.wakleg.market.screen;

import fr.wakleg.market.screen.handler.ConfirmScreenHandler;
import fr.wakleg.market.screen.handler.MarketScreenHandler;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static ScreenHandlerType<MarketScreenHandler> MARKET_SCREEN_HANDLER;
    public static ScreenHandlerType<ConfirmScreenHandler> CONFIRM_SCREEN_HANDLER;

    public static void registerAllScreenHandlers(){
        MARKET_SCREEN_HANDLER = new ScreenHandlerType<>(MarketScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
        CONFIRM_SCREEN_HANDLER = new ScreenHandlerType<>(ConfirmScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
    }
}