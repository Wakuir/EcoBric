package fr.wakleg.market.util;

import com.google.gson.*;
import fr.wakleg.ecobric.Main;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarketData {
    private static List<MarketItem> marketItems = new ArrayList<>();
    public static final String
            URL =  "jdbc:mysql://u446_TFLe3kPIez:esEJVRc5%2BvyA!kDIzqNRmd9!@136.243.63.156:3306/s446_Market",
            USERNAME = "u446_TFLe3kPIez",
            PASSWORD = "esEJVRc5+vyA!kDIzqNRmd9!",
            MARKET_ITEMS_TABLE_NAME = "market_items";

    public static List<MarketItem> getMarketItems(){
        marketItems.clear();
        marketItems = loadDataFromDatabase();
        return marketItems;
    }

    public static String JsonFromItemStack(ItemStack itemStack){
        NbtCompound compound = new NbtCompound();
        itemStack.writeNbt(compound);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(compound);

        if(json != null){
            if(json.contains("field_")){
                int indexOfField = json.indexOf("field_");
                json = json.replace(json.substring(indexOfField, indexOfField + 11), "entries");

                while(json.contains("field_")){
                    indexOfField = json.indexOf("field_");
                    String toReplace = "";
                    if(json.substring(Math.max(0, indexOfField - 10), indexOfField).contains("[")) toReplace = "entries";
                    else toReplace = "value";

                    json = json.replace(json.substring(indexOfField, indexOfField + 11), toReplace);
                }
            }
        }
        return json;
    }

    public static ItemStack ItemStackFromJson(String json) {
        ItemStack itemStack = ItemStack.EMPTY;
        if(json != null) {
            JsonObject valueObject = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("entries");

            if (valueObject.has("id")) {
                String itemIdentifier = valueObject.getAsJsonObject("id").get("value").getAsString();
                itemStack = new ItemStack(Registries.ITEM.get(new Identifier(itemIdentifier)));
            }

            if (valueObject.has("Count")) {
                int itemCount = valueObject.getAsJsonObject("Count").get("value").getAsInt();
                itemStack.setCount(itemCount);
            }

            if (valueObject.has("tag")) {
                JsonObject tagObject = valueObject.getAsJsonObject("tag").getAsJsonObject("entries");

                if (tagObject.has("display")) {
                    JsonObject displayObject = tagObject.getAsJsonObject("display");

                    if (displayObject.has("Name")) {
                        String customName = displayObject.get("Name").getAsString();
                        itemStack.setCustomName(Text.literal(customName));
                    }
                }

                if (tagObject.has("Enchantments")) {
                    JsonArray enchantmentsArray = tagObject.getAsJsonArray("Enchantments");

                    for (JsonElement enchantmentElement : enchantmentsArray) {
                        JsonObject enchantmentObject = enchantmentElement.getAsJsonObject().getAsJsonObject("entries");

                        String enchantmentId = enchantmentObject.getAsJsonObject("id").get("value").getAsString();
                        int enchantmentLevel = enchantmentObject.getAsJsonObject("lvl").get("value").getAsInt();

                        Enchantment enchantment = Registries.ENCHANTMENT.get(new Identifier(enchantmentId));

                        if (enchantment != null) {
                            itemStack.addEnchantment(enchantment, enchantmentLevel);
                        }
                    }
                }

                if (tagObject.has("Unbreakable")) {
                    boolean unbreakable = tagObject.get("Unbreakable").getAsBoolean();
                    itemStack.getOrCreateNbt().putBoolean("Unbreakable", unbreakable);
                }

                if (tagObject.has("HideFlags")) {
                    int hideFlags = tagObject.get("HideFlags").getAsInt();
                    itemStack.getOrCreateNbt().putInt("HideFlags", hideFlags);
                }

                if (tagObject.has("CustomModelData")) {
                    int customModelData = tagObject.get("CustomModelData").getAsInt();
                    itemStack.getOrCreateNbt().putInt("CustomModelData", customModelData);
                }

                if (tagObject.has("Potion")) {
                    String potionIdentifier = tagObject.getAsJsonObject("Potion").get("value").getAsString();
                    itemStack.getOrCreateNbt().putString("Potion", potionIdentifier);
                }

                if(tagObject.has("StoredEnchantments")){
                    JsonArray enchantmentsArray = tagObject.getAsJsonArray("StoredEnchantments");
                    NbtList storedEnchantments = new NbtList();

                    for (JsonElement enchantmentElement : enchantmentsArray) {
                        JsonObject enchantmentObject = enchantmentElement.getAsJsonObject().getAsJsonObject("entries");
                        String enchantmentId = enchantmentObject.getAsJsonObject("id").get("value").getAsString();
                        int enchantmentLevel = enchantmentObject.getAsJsonObject("lvl").get("value").getAsInt();

                        Enchantment enchantment = Registries.ENCHANTMENT.get(new Identifier(enchantmentId));
                        if (enchantment != null) {
                            NbtCompound enchantmentNbt = new NbtCompound();
                            enchantmentNbt.putString("id", enchantmentId);
                            enchantmentNbt.putInt("lvl", enchantmentLevel);
                            storedEnchantments.add(enchantmentNbt);
                        }
                    }

                    itemStack.getOrCreateNbt().put("StoredEnchantments", storedEnchantments);
                }

                if (tagObject.has("CanDestroy")) {
                    JsonArray canDestroyArray = tagObject.getAsJsonArray("CanDestroy");
                    itemStack.getOrCreateNbt().put("CanDestroy", getNbtBlockListFromJsonArray(canDestroyArray));
                }

                if (tagObject.has("CanPlaceOn")) {
                    JsonArray canDestroyArray = tagObject.getAsJsonArray("CanPlaceOn");
                    itemStack.getOrCreateNbt().put("CanPlaceOn", getNbtBlockListFromJsonArray(canDestroyArray));
                }
            }
        }
        return itemStack;
    }

    private static NbtList getNbtBlockListFromJsonArray(JsonArray blockArray){
        NbtList blockList = new NbtList();

        for (JsonElement blockElement : blockArray) {
            JsonObject blockObject = blockElement.getAsJsonObject().getAsJsonObject("entries");
            String blockId = blockObject.getAsJsonObject("id").get("value").getAsString();

            Block block = Registries.BLOCK.get(new Identifier(blockId));
            if(block != null){
                NbtCompound blockNbt = new NbtCompound();
                blockNbt.putString("id", blockId);
                blockList.add(blockNbt);
            }
        }

        return blockList;
    }

    public static void saveJsonToDatabase(MarketItem marketItem){
        try {
            // Create a connection to the MySQL database
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // Create a table (if it doesn't exist) to store the JSON data
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + MARKET_ITEMS_TABLE_NAME + " (id INT AUTO_INCREMENT PRIMARY KEY, json_data TEXT, price INT, owner_uuid TEXT)";
            connection.createStatement().execute(createTableQuery);

            // Insert the JSON data into the table
            String insertDataQuery = "INSERT INTO " + MARKET_ITEMS_TABLE_NAME + " (json_data, price, owner_uuid) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertDataQuery);
            insertStatement.setString(1, JsonFromItemStack(marketItem.getItemStack()));
            insertStatement.setInt(2, marketItem.getPrice());
            insertStatement.setString(3, marketItem.getOwnerUUID());
            insertStatement.executeUpdate();

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeItemFromDatabase(MarketItem item){
        removeItemFromDatabase(item.getId());
    }

    public static void removeItemFromDatabase(int id){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            String deleteQuery = "DELETE FROM " + MARKET_ITEMS_TABLE_NAME + " WHERE (id) = (?)";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<MarketItem> loadDataFromDatabase() {
        try {
            List<MarketItem> marketItemList = new ArrayList<>();
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            String selectDataQuery = "SELECT * FROM " + MARKET_ITEMS_TABLE_NAME;
            ResultSet resultSet = connection.createStatement().executeQuery(selectDataQuery);

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                ItemStack itemStack = ItemStackFromJson(resultSet.getString("json_data"));
                int price = resultSet.getInt("price");
                String ownerUUID = resultSet.getString("owner_uuid");

                marketItemList.add(new MarketItem(id, itemStack, price, ownerUUID));
            }

            connection.close();

            return marketItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
