package fr.wakleg.market.screen.handler;

import fr.wakleg.market.screen.slot.NonInteractiveSlot;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OwnedItemsScreenHandler extends ScreenHandler {
    public static final int ROWS = 6;
    private final Inventory inventory;
    private int page = 0;
    private int buttonsOffset;
    private static List<MarketItem> ownedItems = new ArrayList<>();
    private MarketScreenHandler parent;

    public OwnedItemsScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(ROWS * 9));
    }

    protected OwnedItemsScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(ModScreenHandlers.OWNED_ITEMS_SCREEN_HANDLER, syncId);

        checkSize(inventory, ROWS * 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

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

    private void displayOwnedItems(int page){
        this.page = page;
        for (int i = 0; i < 45; i++) {
            try {
                MarketItem item = this.ownedItems.get(page * 45 + i);
                ItemStack itemStack =  item.getItemStack().copy();
                itemStack.setCustomName(Text.translatable("%s - %s", itemStack.getName(), Text.literal(item.getPrice() + "$").formatted(Formatting.GREEN)));
                inventory.setStack(i, itemStack);
            }catch (IndexOutOfBoundsException e){
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }
        genUI(page);
    }

    private void genUI(int page) {
        ItemStack buttonPrev = new ItemStack(Items.PAPER);
        ItemStack buttonNext = new ItemStack(Items.PAPER);
        ItemStack buttonBack = new ItemStack(Items.BOOK);

        buttonPrev.setCustomName(Text.literal("Previous page").formatted(Formatting.GOLD));
        buttonNext.setCustomName(Text.literal("Next Page").formatted(Formatting.GOLD));
        buttonBack.setCustomName(Text.literal("Back").formatted(Formatting.RED));

        int buttonPrevIndex = ROWS * 9 - 9 + buttonsOffset;
        if(page != 0) inventory.setStack(buttonPrevIndex, buttonPrev);
        else inventory.setStack(buttonPrevIndex, ItemStack.EMPTY);

        int buttonNextIndex = ROWS * 9 - 1 - buttonsOffset;
        if(this.ownedItems.size() > (page + 1) * ROWS * 9 - 9) inventory.setStack(buttonNextIndex, buttonNext);
        else inventory.setStack(buttonNextIndex, ItemStack.EMPTY);

        inventory.setStack(ROWS * 9 - 5, buttonBack);
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
        if(!player.getWorld().isClient){

            ItemStack clickedStack = inventory.getStack(slotIndex);
            if(!clickedStack.isEmpty()){
                if(slotIndex >= 0 && slotIndex <= 44){
                    MarketItem itemClicked = ownedItems.get(this.page * 45 + slotIndex);

                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId1, playerInventory, player1) -> {
                                ConfirmScreenHandler screenHandler = new ConfirmScreenHandler(syncId1, playerInventory);
                                screenHandler.setMarketItem(itemClicked);
                                return screenHandler;
                            }, Text.literal("Confirm Withdraw ?")));
                } else if (slotIndex == ROWS * 9 -5) {
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory(((syncId1, playerInventory, player1) -> parent), Text.literal("Market")));
                }
            }
        }
    }

    public void setItemsList(Stream<MarketItem> marketItemStream) {
        ownedItems.clear();
        marketItemStream.forEach(marketItem -> ownedItems.add(marketItem));
        displayOwnedItems(page);
    }

    public void setButtonsOffset(int buttonsOffset) {
        this.buttonsOffset = buttonsOffset;
    }

    public void setParent(MarketScreenHandler parent) {
        this.parent = parent;
    }
}
