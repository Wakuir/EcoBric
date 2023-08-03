package fr.wakleg.ecobric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("pay")
                .then(CommandManager.argument("receiver", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                .executes(PayCommand::pay))));
    }

    private static int pay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        IEntityDataSaver sender = (IEntityDataSaver) context.getSource().getPlayer();
        ServerPlayerEntity receiver = EntityArgumentType.getPlayer(context, "receiver");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        if(MoneyManager.pay(sender, (IEntityDataSaver) receiver, amount)){
            context.getSource().sendFeedback(() -> Text.translatable("Successfully given %s to %s. You now have %s in your balance.", Text.literal(amount + "$"), Text.literal(receiver.getName() +""), Text.literal(MoneyManager.getMoney(sender) + "$")), false);
        }
        return 0;
    }
}
