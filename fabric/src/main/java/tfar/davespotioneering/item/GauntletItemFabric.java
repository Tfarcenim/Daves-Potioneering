package tfar.davespotioneering.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import tfar.davespotioneering.DavesPotioneering;

public class GauntletItemFabric extends CGauntletItem implements Perspective {

    public GauntletItemFabric(Properties properties) {
        super(Tiers.NETHERITE, 4, -2.8f, properties);
    }

    public static final ResourceLocation ALC_ID = new ResourceLocation(DavesPotioneering.MODID, "item/sprite/potioneer_gauntlet");
    public static final ResourceLocation LIT_ALC_ID = new ResourceLocation(DavesPotioneering.MODID, "item/sprite/lit_potioneer_gauntlet");

    @Override
    public ResourceLocation getGuiModel(boolean active) {
        return active ? LIT_ALC_ID : ALC_ID;
    }
}
