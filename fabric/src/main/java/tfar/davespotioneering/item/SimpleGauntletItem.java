package tfar.davespotioneering.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SimpleGauntletItem extends SwordItem implements Perspective {

    private final ResourceLocation model;

    public SimpleGauntletItem(Tier tier, int damage, float attackSpeed, Properties properties, ResourceLocation model) {
        super(tier, damage, attackSpeed, properties);
        this.model = model;
    }

    @Override
    public ResourceLocation getGuiModel(boolean active) {
        return model;
    }
}
