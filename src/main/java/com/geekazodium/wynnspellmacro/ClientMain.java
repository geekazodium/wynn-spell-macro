package com.geekazodium.wynnspellmacro;

import com.google.common.util.concurrent.AbstractScheduledService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;


public class ClientMain implements ClientModInitializer {
    public static boolean isTesting = true;
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
        //disable for testing
        if(isTesting) {
            return true;
        }
        String ip = server.getServerIp();
        return ip.equals(IP) || ip.equals(BETA_IP);
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
            if (isHoldingBow()){
                tickHandlers(true);
            }else if(isHoldingWeapon()){
                tickHandlers(false);
            }
        }
    }

    private void tickHandlers(boolean b) {
        MacroInputHandler.b = b;
        handler1.tick();
        handler2.tick();
    }

    public static boolean isHoldingWeapon(){
        Item item;
        ItemStack mainHandStack;
        if (client.player != null) {
            mainHandStack = client.player.getInventory().getMainHandStack();
            item = mainHandStack.getItem();
        }else{
            return false;
        }
        if(item == Items.STONE_SHOVEL){
            return !mainHandStack.getName().toString().contains("unidentified");
        }
        return item == Items.BOW ||
                item == Items.SHEARS||
                item == Items.STICK||
                item == Items.IRON_SHOVEL;
    }

    public static boolean isHoldingBow(){
        Item item = null;
        if (client.player != null) {
            item = client.player.getInventory().getMainHandStack().getItem();
        }
        return item == Items.BOW;
    }

    public static boolean canCastSpell(){
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if(hit == null){
            return isNotInInventoryScreen();
        }

        switch(hit.getType()) {
            case MISS:
                return isNotInInventoryScreen();
            case BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                assert client.world != null;
                BlockState blockState = client.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                return (!(block.equals(Blocks.CHEST)||block.equals(Blocks.TRAPPED_CHEST)||block.equals(Blocks.ENDER_CHEST)));
            case ENTITY:
                EntityHitResult entityHit = (EntityHitResult) hit;
                Entity entity = entityHit.getEntity();
                return isEntityNpc(entity);
        }
        return isNotInInventoryScreen();
    }

    private static boolean isNotInInventoryScreen(){
        return !(client.currentScreen instanceof InventoryScreen);
    }

    public static boolean isEntityNpc(Entity entity){
        return false;//TODO make npc look check.
    }
}
