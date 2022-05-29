package tfar.davespotioneering.item;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Identifier;

public class SimpleGauntletItem extends SwordItem implements Perspective {

    private final Identifier model;

    public SimpleGauntletItem(ToolMaterial tier, int damage, float attackSpeed, Settings properties, Identifier model) {
        super(tier, damage, attackSpeed, properties);
        this.model = model;
    }

    @Override
    public Identifier getGuiModel(boolean active) {
        return model;
    }
}
