package tfar.davespotioneering.client.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class StripButton extends Button {
    public StripButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress,DEFAULT_NARRATION);
    }
}
