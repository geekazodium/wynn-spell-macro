package com.geekazodium.wynnspellmacro;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;


public abstract class MacroKeyHandler {
    public static int currentSpellInput = 0;
    private final int inputId;
    private int keyPressTime = 0;
    private boolean canInputSpell = true;
    private static final int holdThreshold = 4;

    public MacroKeyHandler(int id){
        inputId = id;
    }

    protected abstract void inputSpellCommonComponent(MinecraftClient client);
    protected abstract void inputSpell1End(MinecraftClient client);
    protected abstract void inputSpell2End(MinecraftClient client);

    public void tick(MinecraftClient client, KeyBinding useKey) {
        if (currentSpellInput == 0 || currentSpellInput == inputId) {
            if (useKey.isPressed()) {
                if (canInputSpell) {
                    if (keyPressTime > 0) {
                        if (keyPressTime >= holdThreshold) {
                            inputSpell2End(client);
                            canInputSpell = false;
                            keyPressTime = 0;
                            currentSpellInput = 0;
                        }
                    } else {
                        inputSpellCommonComponent(client);
                        currentSpellInput = inputId;
                    }
                    keyPressTime += 1;
                }
            } else {
                if (keyPressTime > 0) {
                    if (keyPressTime <= holdThreshold && canInputSpell) {
                        inputSpell1End(client);
                        canInputSpell = false;
                        currentSpellInput = 0;
                    }
                    keyPressTime = 0;
                } else {
                    canInputSpell = true;
                }
            }
        }
    }
}
