package com.geekazodium.wynnspellmacro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


public class ClientMain implements ClientModInitializer {
    MacroHandler macro = new MacroHandler(2);
    //TODO make input buffer as opposed to instantly inputting 2 actions
    static KeyBinding keyBinding2 = new KeyBinding(
            "wynnspellmacro.key2",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Q,
            "wynnspellmacro.spells"
    );

    private MacroKeyHandler handler1;

    private MacroKeyHandler handler2;

    @Override
    public void onInitializeClient() {
        handler1 = new MacroKeyHandler(1) {
            @Override
            protected void inputSpellCommonComponent(MinecraftClient client) {
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            }

            @Override
            protected void inputSpell1End(MinecraftClient client) {
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            }

            @Override
            protected void inputSpell2End(MinecraftClient client) {
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            }
        };
        handler2 = new MacroKeyHandler(2) {
            @Override
            protected void inputSpellCommonComponent(MinecraftClient client) {
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            }

            @Override
            protected void inputSpell1End(MinecraftClient client) {
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            }

            @Override
            protected void inputSpell2End(MinecraftClient client) {
                macro.bufferEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            }
        };
        KeyBindingHelper.registerKeyBinding(keyBinding2);
        ClientTickEvents.START_CLIENT_TICK.register(this::onStartTick);
    }

    private void onStartTick(MinecraftClient client) {
        if(client.player!=null){
            macro.tick(client);
            if(client.player.getInventory().getMainHandStack().getItem() == Items.STONE_SHOVEL){
                handler1.tick(client,client.options.useKey);
                while (client.options.useKey.wasPressed()){}
                ((ClientInputActionAccessor)client).setItemUseCooldown(2);
                handler2.tick(client,keyBinding2);
                if(MacroKeyHandler.currentSpellInput!=0){
                    while (client.options.attackKey.wasPressed()){}
                }
            }
        }
    }
}
