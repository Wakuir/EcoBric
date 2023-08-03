package fr.wakleg.market.util;

import net.minecraft.item.ItemStack;

public class MarketItem {
    private int id;
    private ItemStack itemStack;
    private int price;
    private String ownerUUID;

    public MarketItem(int id, ItemStack itemStack, int price, String ownerUUID){
        this.id = id;
        this.itemStack = itemStack;
        this.price = price;
        this.ownerUUID = ownerUUID;
    }

    public int getId() {
        return id;
    }
    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getPrice() {
        return price;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }
}
