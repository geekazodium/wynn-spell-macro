package com.geekazodium.wynnspellmacro.mixins;

import com.geekazodium.wynnspellmacro.ClientInputActionAccessor;
import com.geekazodium.wynnspellmacro.ClientMain;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.geekazodium.wynnspellmacro.ClientMain.canCastSpell;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements ClientInputActionAccessor {
    @Shadow protected abstract boolean doAttack();

    @Shadow protected abstract void doItemUse();

    @Redirect(method = "handleInputEvents",
            target = @Desc(value = "doAttack", ret=boolean.class),
            at = @At(value = "INVOKE",target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")
    )
    public boolean onInputAttack(MinecraftClient instance){
        if(ClientMain.macro.occupiedByAction){
            return false;
        }
        ClientPlayerEntity player = instance.player;
        if(player != null) {
            if(player.getInventory().getMainHandStack().getItem() == Items.BOW){
                doItemUse();
            }else{
                doAttack();
            }
        }
        return false;
    }
    @Redirect(method = "handleInputEvents",
            target = @Desc(value = "doItemUse"),
            at = @At(value = "INVOKE",target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V")
    )
    public void onTriggerUse(MinecraftClient instance){
        ClientPlayerEntity player = instance.player;
        if(player != null) {
            if(ClientMain.isHoldingWeapon()) {
                if(!canCastSpell()){
                    doItemUse();
                    return;
                }
                ClientMain.handler1.onPress();
            }else{
                doItemUse();
            }
        }
    }

    @Override
    public void _doAttack(){
        doAttack();
    }

    @Override
    public void _doItemUse() {
        doItemUse();
    }
}
