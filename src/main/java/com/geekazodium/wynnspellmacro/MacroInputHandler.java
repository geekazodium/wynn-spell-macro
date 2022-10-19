package com.geekazodium.wynnspellmacro;

import com.geekazodium.wynnspellmacro.mixins.MinecraftClientMixin;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import static com.geekazodium.wynnspellmacro.ClientMain.canCastSpell;

public class MacroInputHandler {
    public static final int START_PRESS = 1;
    public static final int NO_EVENT = 0;
    public static final int END_PRESS = 2;
    public static final int TAP = 3;
    private final KeyBinding keyBinding;
    private final MacroHandler macro;
    private int wasPressed;
    private boolean isPressing = false;
    public static boolean b = false;
    private boolean pressing = false;
    private boolean complete = false;
    int eventType = 0;

    private int pressDuration = 0;
    private static final int holdThreshold = 5;

    public MacroInputHandler(KeyBinding keybinding, MacroHandler macro){
        this.keyBinding = keybinding;
        this.macro = macro;
    }
    protected void inputKey1(){
        macro.bufferEvent(b? GLFW.GLFW_MOUSE_BUTTON_LEFT:GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }
    protected void inputKey2(){
        macro.bufferEvent(!b?GLFW.GLFW_MOUSE_BUTTON_LEFT:GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }
    public void inputCommonSeq(){
        inputKey1();
        inputKey1();
    }

    private boolean wasPressed(){
        if(wasPressed<=0){
            if(keyBinding.wasPressed()){
                resetKey();
                return true;
            }
            return false;
        }
        resetKey();
        return true;
    }

    private void resetKey() {
        wasPressed = 0;
        while (keyBinding.wasPressed()){}
    }

    private boolean isPressed(){
        return keyBinding.isPressed();
    }

    private void updateIsPressing(){
        this.eventType = NO_EVENT;
        if(isPressed()){
            resetKey();
            if(!isPressing){
                this.eventType = START_PRESS;
            }
            isPressing = true;
        }else{
            if(isPressing){
                this.eventType = END_PRESS;
            }
            if(wasPressed()){
                if(!isPressing){
                    this.eventType = TAP;
                }
            }
            isPressing = false;
        }
    }

    public void tick(){
        if(!canCastSpell()){
            return;
        }
        updateIsPressing();
        if(pressing){
            pressDuration++;
            if(pressDuration>holdThreshold + 2){
                keyBinding.setPressed(false);
            }
        }
        if(eventType == NO_EVENT){
            return;
        }
        if(eventType == START_PRESS){
            if(macro.occupiedByAction){
                return;
            }
            complete = false;
            macro.forcedOccupiedByAction = true;
            macro.setOccupiedByAction(true);
            inputCommonSeq();
            //ClientMain.client.player.sendChatMessage("start press");
            pressing = true;
        } else if (eventType == END_PRESS) {
            if(!pressing){
                return;
            }
            if(complete){
                return;
            }
            //ClientMain.client.player.sendChatMessage("end press");
            pressing = false;
            endSpell();
        }else if(eventType == TAP){
            if(macro.occupiedByAction){
                return;
            }
            macro.setOccupiedByAction(true);
            inputCommonSeq();
            inputKey1();
            //ClientMain.client.player.sendChatMessage("tap");
        }
    }

    private void endSpell() {
        complete = true;
        if(pressDuration>=holdThreshold){
            inputKey2();
        }else{
            inputKey1();
        }
        pressDuration = 0;
        macro.forcedOccupiedByAction = false;
    }

    public void onPress(){
        if(isPressing){
            return;
        }
        wasPressed +=1;
    }
}
