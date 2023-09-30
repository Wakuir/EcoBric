package fr.wakleg.market.event;

import fr.wakleg.ecobric.util.IEntityDataSaver;
import fr.wakleg.ecobric.util.MoneyManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

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

                String createTableQuery = "CREATE TABLE IF NOT EXISTS " + OFFLINE_MONEY_TABLE + " (player_uuid TEXT, amount INT)";
                connection.createStatement().execute(createTableQuery);

                String selectQuery = "SELECT amount FROM " + OFFLINE_MONEY_TABLE + " WHERE player_uuid = (?)";
                PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                selectStatement.setString(1, player.getUuidAsString());
                ResultSet resultSet = selectStatement.executeQuery();

                if(resultSet.next()){
                    MoneyManager.add((IEntityDataSaver) player, resultSet.getInt("amount"));
                    String deleteQuery = "DELETE FROM " + OFFLINE_MONEY_TABLE + " WHERE (player_uuid) = (?)";
                    PreparedStatement statement = connection.prepareStatement(deleteQuery);
                    statement.setString(1, player.getUuidAsString());
                    statement.executeUpdate();

                    player.sendMessage(Text.literal("Vos items on été vendus pour un montant de " + resultSet.getInt("amount") + "$"));
                }

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
