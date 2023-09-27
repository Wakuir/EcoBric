package fr.wakleg.market;

import fr.wakleg.Saver;
import fr.wakleg.market.command.MarketCommand;
import fr.wakleg.market.command.SellCommand;
import fr.wakleg.market.event.LoginHandler;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class Server implements DedicatedServerModInitializer {
    public static Saver saver;
    @Override
    public void onInitializeServer() {
        saver = new Saver(new File(FabricLoader.getInstance().getConfigDir().toFile(), "market.properties"));
        saver.load();
        System.out.println(saver.get("DbUrl"));
        System.out.println(saver.get("DbUser"));
        System.out.println(saver.get("DbPassword"));
        System.out.println(saver.get("DbTableName"));
        String[] args = {"DbUrl","DbUser","DbPassword","DbTableName"};
        for(String arg : args){
            if(saver.get(arg) == null){
                saver.set(arg, "");
            }
        }

    }
}
