package fr.wakleg.market.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.wakleg.market.screen.handler.MarketScreenHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MarketCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("market")
                .executes(MarketCommand::market));
    }

    private static int market(CommandContext<ServerCommandSource> context) {
        try {
            context.getSource().getPlayer().openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, playerInventory, player) -> new MarketScreenHandler(syncId, playerInventory),
                    Text.literal("Market")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
