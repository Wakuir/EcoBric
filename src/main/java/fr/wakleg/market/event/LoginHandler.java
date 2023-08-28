package fr.wakleg.market.event;

import fr.wakleg.ecobric.Main;
import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.sql.*;

import static fr.wakleg.ecobric.util.MoneyManager.OFFLINE_MONEY_TABLE;
import static fr.wakleg.market.util.MarketData.*;

public class LoginHandler implements ServerEntityEvents.Load {
    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        if (entity instanceof ServerPlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            try {
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String createTableQuery = "CREATE TABLE IF NOT EXISTS " + MARKET_ITEMS_TABLE_NAME + " (id INT AUTO_INCREMENT PRIMARY KEY, json_data TEXT, price INT, owner_uuid TEXT)";
                connection.createStatement().execute(createTableQuery);

                String selectQuery = "SELECT player_uuid, amount FROM " + OFFLINE_MONEY_TABLE;
                ResultSet resultSet = connection.createStatement().executeQuery(selectQuery);

                while (resultSet.next()){
                    if (player.getUuidAsString().equals(resultSet.getString("player_uuid"))){
                        MoneyManager.add((IEntityDataSaver) player, resultSet.getInt("amount"));
                        String deleteQuery = "DELETE FROM " + OFFLINE_MONEY_TABLE + " WHERE (player_uuid) = (?)";
                        PreparedStatement statement = connection.prepareStatement(deleteQuery);
                        statement.setString(1, player.getUuidAsString());
                        statement.executeUpdate();
                    }
                }

                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
