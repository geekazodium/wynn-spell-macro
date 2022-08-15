package com.geekazodium.wynnspellmacro.mixins;

import com.geekazodium.wynnspellmacro.ClientInputActionAccessor;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements ClientInputActionAccessor {
    @Shadow protected abstract boolean doAttack();

    @Shadow protected abstract void doItemUse();

    @Shadow private int itemUseCooldown;

    public void _doAttack(){
        doAttack();
    }

    @Override
    public void _doItemUse() {
        doItemUse();
    }

    @Override
    public void setItemUseCooldown(int i) {
        itemUseCooldown = i;
    }
}
