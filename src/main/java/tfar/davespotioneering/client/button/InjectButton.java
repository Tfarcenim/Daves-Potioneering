package tfar.davespotioneering.client.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button.OnPress;

public class InjectButton extends Button {

    protected InjectButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress,DEFAULT_NARRATION);
    }
}
