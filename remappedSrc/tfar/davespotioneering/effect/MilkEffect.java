package tfar.davespotioneering.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class MilkEffect extends InstantStatusEffect {
    public MilkEffect(StatusEffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }


    @Override
    public void applyUpdateEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.clearStatusEffects();
    }
}
