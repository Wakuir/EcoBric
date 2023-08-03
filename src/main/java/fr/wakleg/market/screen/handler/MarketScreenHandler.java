package fr.wakleg.market.screen.handler;

import fr.wakleg.ecobric.Main;
import fr.wakleg.market.screen.ModScreenHandlers;
import fr.wakleg.market.screen.NonInteractiveSlot;
import fr.wakleg.market.util.MarketData;
import fr.wakleg.market.util.MarketItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class MarketScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    public static final int ROWS = 6;


    public MarketScreenHandler(int syncId, PlayerInventory inventory){
        this(syncId, inventory, new SimpleInventory(ROWS*9));
    }

    protected MarketScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.MARKET_SCREEN_HANDLER, syncId);
        checkSize(inventory, ROWS * 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);


        int i = 0;
        for (MarketItem item : MarketData.getMarketItems()) {
            ItemStack itemStack =  item.getItemStack();
            itemStack.setCustomName(Text.translatable("%s - %s", itemStack.getName(), Text.literal(item.getPrice() + "$").formatted(Formatting.GREEN)));
            inventory.setStack(i++, itemStack);
        }

        //Market Slots
        i = (ROWS - 4) * 18;
        for (int j = 0; j < ROWS; ++j) {
            for (int k = 0; k < 9; ++k) {
                int index = k + j * 9;
                this.addSlot(new NonInteractiveSlot(inventory, index, 8 + k * 18, 18 + j * 18));
            }
        }

        //Player Inventory
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        //Player Hotbar
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        ItemStack clickedStack = inventory.getStack(slotIndex);
        if(!clickedStack.isEmpty()) {
            if (!player.getWorld().isClient && slotIndex >= 0 && slotIndex <= 54) {
                player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                        (syncId1, playerInventory, player1) -> {
                            ConfirmScreenHandler screenHandler = new ConfirmScreenHandler(syncId1, playerInventory);
                            Main.LOGGER.info("" + MarketData.getMarketItems());
                            screenHandler.setMarketItem(MarketData.getMarketItems().get(slotIndex));
                            return screenHandler;
                        }, Text.literal("Confirm purchase ?")));
            }
        }
    }
}