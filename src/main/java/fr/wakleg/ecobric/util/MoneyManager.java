package fr.wakleg.ecobric.util;


import fr.wakleg.ecobric.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static fr.wakleg.market.util.MarketData.*;

public class MoneyManager {
    public static final String OFFLINE_MONEY_TABLE = "offline_money";

    public static final String KEY_MONEY = "money";

    public static int getMoney(IEntityDataSaver player){
        return player.getPersistentData().getInt(KEY_MONEY);
    }

    public static void add(IEntityDataSaver player, int amount){
        player.getPersistentData().putInt(KEY_MONEY, getMoney(player) + amount);
    }

    public static boolean take(IEntityDataSaver player, int amount){
        if(getMoney(player) - amount >= 0){
            player.getPersistentData().putInt(KEY_MONEY, getMoney(player) - amount);
            return true;
        }
        return false;
    }

    public static boolean withdraw(ServerPlayerEntity player, int amount){
        if(take((IEntityDataSaver) player, amount)){
            player.getInventory().insertStack(new ItemStack(ModItems.BANKNOTE, amount));
            return true;
        }
        return false;
    }

    public static boolean pay(IEntityDataSaver sender, IEntityDataSaver receiver, int amount){
        if(take(sender, amount)) {
            add(receiver, amount);
            return true;
        }
        return false;
    }

    public static boolean payByUUID(PlayerEntity sender, String receiverUUID, int amount){
        if(take((IEntityDataSaver) sender, amount)) {
            for (PlayerEntity playerEntity : sender.getServer().getPlayerManager().getPlayerList()) {
                if (playerEntity.getUuidAsString() == receiverUUID) {
                    if (pay((IEntityDataSaver) sender, (IEntityDataSaver) playerEntity, amount)) {
                        return true;
                    }
                }
            }
            MoneyManager.payOfflinePlayer(receiverUUID, amount);
            return true;
        }
        return false;
    }

    private static void payOfflinePlayer(String receiverUUID, int amount){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + OFFLINE_MONEY_TABLE + " (player_uuid TEXT, amount INT)";
            connection.createStatement().execute(createTableQuery);

            String insertDataQuery = "INSERT INTO " + OFFLINE_MONEY_TABLE + " (player_uuid, amount) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertDataQuery);
            insertStatement.setString(1, receiverUUID);
            insertStatement.setInt(2, amount);
            insertStatement.executeUpdate();

            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
