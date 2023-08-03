package fr.wakleg.ecobric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class WithdrawCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("withdraw")
                .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                        .executes(WithdrawCommand::withdraw)));
    }

    private static int withdraw(CommandContext<ServerCommandSource> context) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ServerPlayerEntity player = context.getSource().getPlayer();

        boolean withdraw = MoneyManager.withdraw(player, amount);

        if(withdraw){
            context.getSource().sendFeedback(() -> Text.translatable("Successfully withdrawn %s from your balance. You now have %s", Text.literal(amount + "$"), Text.literal(MoneyManager.getMoney((IEntityDataSaver) player) + "$")), false);
            return 0;
        }

        return -1;
    }
}
