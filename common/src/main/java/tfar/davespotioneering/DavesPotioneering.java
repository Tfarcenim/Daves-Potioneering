package tfar.davespotioneering;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import tfar.davespotioneering.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class DavesPotioneering {

    public static final String MOD_NAME = "Dave's Potioneering";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String MODID = "davespotioneering";

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void commonSetup() {
    }

    public static void onEat(Player player, ItemStack stack) {
        List<MobEffectInstance> mobEffectInstances = PotionUtils.getMobEffects(stack);
        for (MobEffectInstance effectInstance : mobEffectInstances) {
            player.addEffect(new MobEffectInstance(effectInstance.getEffect(), Math.max(effectInstance.getDuration() / 8, 1), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.showIcon()));
        }
    }


}