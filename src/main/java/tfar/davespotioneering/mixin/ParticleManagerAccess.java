package tfar.davespotioneering.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccess {

    @Invoker("addParticle")
    Particle $makeParticle(ParticleEffect particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

}
