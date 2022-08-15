package com.geekazodium.wynnspellmacro;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;

public class MacroHandler {
    private final LinkedList<Integer> bufferedEvents = new LinkedList<>();
    private final int inputInterval;
    private int inputTimer = 0;

    public MacroHandler(int inputInterval){
        this.inputInterval = inputInterval;
    }

    public void bufferEvent(int id){
        bufferedEvents.add(id);
    }

    public void tick(MinecraftClient client){
        if(inputTimer>0){
            inputTimer--;
        }else if(bufferedEvents.size()>0){
            int i = bufferedEvents.pop();
            if(i == GLFW.GLFW_MOUSE_BUTTON_1){
                ((ClientInputActionAccessor) client)._doAttack();
            }else if(i == GLFW.GLFW_MOUSE_BUTTON_2){
                ((ClientInputActionAccessor) client)._doItemUse();
            }
            inputTimer = inputInterval;
        }
    }
}
