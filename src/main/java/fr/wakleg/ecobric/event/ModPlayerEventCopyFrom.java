package fr.wakleg.ecobric.event;

import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModPlayerEventCopyFrom implements ServerPlayerEvents.CopyFrom{
    @Override
    public void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        IEntityDataSaver original = (IEntityDataSaver) oldPlayer;
        IEntityDataSaver player = (IEntityDataSaver) newPlayer;

        player.getPersistentData().putDouble(MoneyManager.KEY_MONEY, MoneyManager.getMoney(original));
    }
}
