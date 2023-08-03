package fr.wakleg.ecobric.item.custom;

import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BanknoteItem extends Item {
    public BanknoteItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldItemStack = user.getStackInHand(hand);
        user.getInventory().removeOne(heldItemStack);
        MoneyManager.add((IEntityDataSaver) user, heldItemStack.getCount());

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
