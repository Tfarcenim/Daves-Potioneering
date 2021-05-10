package tfar.davespotioneering.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;

public class MilkEffect extends InstantEffect {
    public MilkEffect(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }


    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.clearActivePotions();
    }
}
