package fr.wakleg.ecobric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MoneyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("money")
                .executes(MoneyCommand::money));
    }

    private static int money(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.translatable("You have %s in your balance.", Text.literal(MoneyManager.getMoney((IEntityDataSaver) context.getSource().getPlayer()) + "$")), false);
        return 0;
    }
}
