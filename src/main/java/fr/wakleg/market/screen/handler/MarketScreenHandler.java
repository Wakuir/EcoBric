package fr.wakleg.market.screen.handler;

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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class MarketScreenHandler extends ScreenHandler {
    public static final int ROWS = 6,
            BUTTONS_OFFSET = 1, // [0;3]
            BUTTON_PREV_INDEX = ROWS * 9 - 9 + BUTTONS_OFFSET,
            BUTTON_NEXT_INDEX = ROWS * 9 - 1 - BUTTONS_OFFSET,
            BUTTON_OWNED_ITEMS_INDEX = ROWS * 9 - 5;
    private final Inventory inventory;
    private int page = 0;
    private static List<MarketItem> marketItems;

    public MarketScreenHandler(int syncId, PlayerInventory inventory){
        this(syncId, inventory, new SimpleInventory(ROWS*9));
    }

    protected MarketScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.MARKET_SCREEN_HANDLER, syncId);

        checkSize(inventory, ROWS * 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.marketItems = MarketData.getMarketItems();

        //Market Slots
        for (int j = 0; j < ROWS; ++j) {
            for (int k = 0; k < 9; ++k) {
                int index = k + j * 9;
                this.addSlot(new NonInteractiveSlot(inventory, index, 8 + k * 18, 18 + j * 18));
            }
        }

        displayMarketItems(page);

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

    private void displayMarketItems(int page) {
        this.page = page;
        for (int i = 0; i < 45; i++) {
            try {
                MarketItem item = this.marketItems.get(page * 45 + i);
                ItemStack itemStack =  item.getItemStack().copy();
                itemStack.setCustomName(Text.translatable("%s - %s", itemStack.getName(), Text.literal(item.getPrice() + "$").formatted(Formatting.GREEN)));
                inventory.setStack(i, itemStack);
            }catch (IndexOutOfBoundsException e){
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }
        genUI();
    }

    private void genUI() {
        ItemStack buttonPrev = new ItemStack(Items.PAPER);
        ItemStack buttonNext = new ItemStack(Items.PAPER);
        ItemStack buttonOwnedItems = new ItemStack(Items.BOOK);

        buttonPrev.setCustomName(Text.literal("Previous page").formatted(Formatting.GOLD));
        buttonNext.setCustomName(Text.literal("Next Page").formatted(Formatting.GOLD));
        buttonOwnedItems.setCustomName(Text.literal("Your Items").formatted(Formatting.LIGHT_PURPLE));

        inventory.setStack(BUTTON_PREV_INDEX, buttonPrev);
        inventory.setStack(BUTTON_NEXT_INDEX, buttonNext);
        inventory.setStack(BUTTON_OWNED_ITEMS_INDEX, buttonOwnedItems);

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
        if(!player.getWorld().isClient) {

            ItemStack clickedStack = inventory.getStack(slotIndex);
            if (!clickedStack.isEmpty()) {
                if(slotIndex >= 0 && slotIndex <= 44) {
                    MarketItem itemClicked = marketItems.get(this.page * 45 + slotIndex);
                    String confirmTitle = "";
                    if(itemClicked.getOwnerUUID().equals(player.getUuidAsString())) confirmTitle = "Confirm Withdraw ?";
                    else confirmTitle = "Confirm Purchase ?";

                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId1, playerInventory, player1) -> {
                                ConfirmScreenHandler screenHandler = new ConfirmScreenHandler(syncId1, playerInventory);
                                screenHandler.setMarketItem(itemClicked);
                                screenHandler.setParent(this);
                                return screenHandler;
                            }, Text.literal(confirmTitle)));
                }
                 else if (slotIndex == BUTTON_PREV_INDEX) {
                        if(page != 0)
                            displayMarketItems(this.page - 1);
                }
                 else if (slotIndex == BUTTON_NEXT_INDEX) {
                    if(this.marketItems.size() > (page + 1) * ROWS * 9 - 9)
                        displayMarketItems(this.page + 1);
                }
                 else if(slotIndex == BUTTON_OWNED_ITEMS_INDEX){
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            ((syncId1, playerInventory, player1) -> {
                                OwnedItemsScreenHandler screenHandler = new OwnedItemsScreenHandler(syncId1, playerInventory);
                                screenHandler.setItemsList(marketItems.stream().filter(marketItem -> marketItem.getOwnerUUID().equals(player.getUuidAsString())));
                                screenHandler.setButtonsOffset(BUTTONS_OFFSET);
                                screenHandler.setParent(this);
                                return screenHandler;
                            }),
                            Text.literal("Your Items")));
                }
            }
        }
    }
}