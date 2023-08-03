package fr.wakleg.market.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.wakleg.ecobric.Main;
import fr.wakleg.market.util.MarketData;
import fr.wakleg.market.util.MarketItem;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SellCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("sell")
                .then(CommandManager.argument("price", IntegerArgumentType.integer())
                        .executes(SellCommand::sell)
                        .then(CommandManager.argument("count", IntegerArgumentType.integer())
                                .executes(SellCommand::sell))));
    }

    private static int sell(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack heldStack = inventory.getStack(inventory.selectedSlot);

        if(!heldStack.isEmpty()){
            try {
                heldStack.setCount(IntegerArgumentType.getInteger(context, "count"));
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }

            int price = IntegerArgumentType.getInteger(context, "price");
            String ownerUUID = player.getUuidAsString();

            int contained = contained(heldStack, player.getInventory());
            if (contained != -1){
                MarketData.saveJsonToDatabase(new MarketItem(-1, heldStack, price, ownerUUID));
                context.getSource().sendFeedback(() -> Text.literal("Your item has been saved to the market"), false);
                player.getInventory().removeOne(heldStack);
            }
            else{
                context.getSource().sendFeedback(() -> Text.literal("Please hold these items if you want to sell them."), false);
                return -1;
            }

        }else {
            context.getSource().sendFeedback(() -> Text.literal("You can't make money with nothing, go to work now."), false);
            return -1;
        }

        return 0;
    }

    static int contained(ItemStack itemStack, PlayerInventory inventory){
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack inventoryStack = inventory.getStack(i);
            if (itemStack.getItem() == inventoryStack.getItem() && inventoryStack.getCount() >= itemStack.getCount()){
                return i;
            }
        }

        return -1;
    }
}