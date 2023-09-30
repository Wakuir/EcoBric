package fr.wakleg.market;

import fr.wakleg.Saver;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class Server implements DedicatedServerModInitializer {
    public static Saver saver;

    @Override
    public void onInitializeServer() {
        saver = new Saver(new File(FabricLoader.getInstance().getConfigDir().toFile(), "market.properties"));
        saver.load();
        String[] args = {"DbUrl", "DbUser", "DbPassword", "DbMarketTableName", "DbOfflineMoneyTableName"};
        for(String arg : args){
            if(saver.get(arg) == null){
                saver.set(arg, "");
            }
        }
    }
}
