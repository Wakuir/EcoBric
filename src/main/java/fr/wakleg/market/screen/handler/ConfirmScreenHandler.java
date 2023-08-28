package fr.wakleg.market.screen.handler;

import fr.wakleg.ecobric.util.MoneyManager;
import fr.wakleg.market.screen.slot.NonInteractiveSlot;
import fr.wakleg.market.util.MarketData;
import fr.wakleg.market.util.MarketItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfirmScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    public static final int ROWS = 3;
    public static final int
            ITEM_SLOT = 4,
            CANCEL_SLOT = 10,
            CONFIRM_SLOT = 16;
    private MarketItem marketItem;
    private MarketScreenHandler parent;

    public ConfirmScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(ROWS * 9));
    }

    protected ConfirmScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(ModScreenHandlers.CONFIRM_SCREEN_HANDLER, syncId);
        checkSize(inventory, ROWS * 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        ItemStack cancelStack = new ItemStack(Items.RED_STAINED_GLASS_PANE),
                confirmStack = new ItemStack(Items.GREEN_STAINED_GLASS_PANE);
        cancelStack.setCustomName(Text.literal("Cancel").formatted(Formatting.RED));
        confirmStack.setCustomName(Text.literal("Confirm").formatted(Formatting.GREEN));

        inventory.setStack(CANCEL_SLOT, cancelStack);
        inventory.setStack(CONFIRM_SLOT, confirmStack);

        //Market Slots
        for (int j = 0; j < ROWS; ++j) {
            for (int k = 0; k < 9; ++k) {
                int index = k + j * 9;
                this.addSlot(new NonInteractiveSlot(inventory, index, 8 + k * 18, 18 + j * 18));
            }
        }

        //Player Inventory
        int i = (ROWS - 4) * 18;
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
        if (!player.getWorld().isClient()) {
            if (!inventory.getStack(slotIndex).isEmpty()) {
                switch (slotIndex) {
                    case CONFIRM_SLOT:
                        if(MoneyManager.payByUUID(player, marketItem.getOwnerUUID().toString(), marketItem.getPrice())) {
                            player.getInventory().insertStack(marketItem.getItemStack());
                            MarketData.removeItemFromDatabase(marketItem);
                            tryClose(player);
                        }
                        else player.sendMessage(Text.literal("You don't have enough money to buy this.").formatted(Formatting.RED));

                        break;
                    case CANCEL_SLOT:
                        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(((syncId1, playerInventory, player1) -> parent), Text.literal("Market")));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void tryClose(PlayerEntity player){
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            serverPlayerEntity.closeHandledScreen();
        }
    }

    public void setMarketItem(MarketItem marketItem){
        this.marketItem = marketItem;
        ItemStack itemStack = marketItem.getItemStack().copy();
        itemStack.setCustomName(Text.translatable("%s - %s", itemStack.getName(), Text.literal(marketItem.getPrice() + "$").formatted(Formatting.GREEN)));
        inventory.setStack(ITEM_SLOT, itemStack);
    }

    public void setParent(MarketScreenHandler parent) {
        this.parent = parent;
    }
}
