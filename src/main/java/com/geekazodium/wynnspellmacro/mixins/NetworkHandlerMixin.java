package com.geekazodium.wynnspellmacro.mixins;

import com.geekazodium.wynnspellmacro.ClientMain;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class NetworkHandlerMixin {
    @Inject(method = "sendPacket",at = @At("HEAD"), cancellable = true)
    public void onInputSwapHands(Packet<?> packet, CallbackInfo ci){
        if(!ClientMain.isOnWynncraft()){
            return;
        }
        if(packet instanceof PlayerActionC2SPacket action){
            if(action.getAction().equals(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND)){
                ClientMain.handler2.onPress();
               // System.out.println(ClientMain.handler2.wasPressed);*/
                ci.cancel();
            }
        }
    }
}
