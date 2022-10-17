package com.geekazodium.wynnspellmacro;

import com.google.common.util.concurrent.AbstractScheduledService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import org.lwjgl.glfw.GLFW;


public class ClientMain implements ClientModInitializer {
    public static final String IP = "play.wynncraft.net";
    public static final String BETA_IP = "beta.wynncraft.net";
    public static MinecraftClient client;
    public static MacroHandler macro = new MacroHandler(2);
    public static MacroInputHandler handler1;

    public static MacroInputHandler handler2;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(this::onStartTick);
    }

    public static boolean isOnWynncraft(){
        if(client.world == null){
            return false;
        }
        MinecraftServer server = client.world.getServer();
        if(server == null){
            return false;
        }
        /* //disable for testing
        String ip = server.getServerIp();
        if(!ip.equals(IP)&&!ip.equals(BETA_IP)){
            return;
        }
        */
        return true;
    }

    private void onStartTick(MinecraftClient client) {
        if(ClientMain.client == null){
            handler1 = new MacroInputHandler(client.options.useKey,macro);
            handler2 = new MacroInputHandler(client.options.swapHandsKey,macro){
                @Override
                public void inputCommonSeq() {
                    inputKey1();
                    inputKey2();
                }
            };
        }
        ClientMain.client = client;
        ClientPlayerEntity player = client.player;
        if(player !=null){
            macro.tick(client);
            if(player.getInventory().getMainHandStack().getItem() == Items.STONE_SHOVEL){
                tickHandlers(false);
            }
            if (player.getInventory().getMainHandStack().getItem() == Items.BOW){
                tickHandlers(true);
            }
        }
    }

    private void tickHandlers(boolean b) {
        MacroInputHandler.b = b;
        handler1.tick();
        handler2.tick();
        //handler2.tick(client, client.options.swapHandsKey,b);
    }
}
