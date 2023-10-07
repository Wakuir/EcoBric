package fr.wakleg.ecobric.util;


import fr.wakleg.ecobric.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import static fr.wakleg.market.Server.saver;

import java.sql.*;

import static fr.wakleg.market.util.MarketData.*;

public class MoneyManager {
    public static final String OFFLINE_MONEY_TABLE = saver.get("DbOfflineMoneyTableName");

    public static final String KEY_MONEY = "money";

    public static void initDatabase() {
        try {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + OFFLINE_MONEY_TABLE + " (player_uuid TEXT, amount INT)";
            connection.createStatement().execute(createTableQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static boolean payByUUID(PlayerEntity sender, String receiverUUID, int amount){
        if(take((IEntityDataSaver) sender, amount)) {
            for (ServerPlayerEntity serverPlayerEntity : sender.getServer().getPlayerManager().getPlayerList()) {
                if (serverPlayerEntity.getUuidAsString().equals(receiverUUID)) {
                    add((IEntityDataSaver) serverPlayerEntity, amount);
                    return true;
                }
            }
            addOfflinePlayer(receiverUUID, amount);
            return true;
        }
        return false;
    }

    private static void addOfflinePlayer(String receiverUUID, int amount){
        try {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + OFFLINE_MONEY_TABLE + " (player_uuid TEXT, amount INT)";
            connection.createStatement().execute(createTableQuery);

            String selectQuery = "SELECT amount FROM " + OFFLINE_MONEY_TABLE + " WHERE player_uuid = (?)";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, receiverUUID);
            ResultSet results = selectStatement.executeQuery();

            if(results.next()){
                String updateQuery = "UPDATE " + OFFLINE_MONEY_TABLE + " SET amount = (?) WHERE player_uuid = (?)";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, results.getInt("amount") + amount);
                updateStatement.setString(2, receiverUUID);
                updateStatement.execute();
            }
            else{
                String insertDataQuery = "INSERT INTO " + OFFLINE_MONEY_TABLE + " (player_uuid, amount) VALUES (?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertDataQuery);
                insertStatement.setString(1, receiverUUID);
                insertStatement.setInt(2, amount);
                insertStatement.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
