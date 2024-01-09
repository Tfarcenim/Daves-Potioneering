package tfar.davespotioneering.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class ModPotions {

    public static final Potion MILK = new Potion(new MobEffectInstance(ModEffects.MILK));

    public static final MobEffectInstance INVIS_2 =  new MobEffectInstance(MobEffects.INVISIBILITY, 1800,1,false,false);

    public static final Potion STRONG_INVISIBILITY = new Potion("invisibility",INVIS_2);
}
