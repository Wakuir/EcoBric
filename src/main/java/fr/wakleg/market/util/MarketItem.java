package fr.wakleg.market.util;

import net.minecraft.item.ItemStack;

public record MarketItem(int id, ItemStack itemStack, int price, String ownerUUID) {}
