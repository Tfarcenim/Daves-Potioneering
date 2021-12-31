package tfar.davespotioneering.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.IParticleData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccess {

    @Invoker("makeParticle")
    Particle $makeParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

}
