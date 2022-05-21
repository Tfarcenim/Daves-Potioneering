package tfar.davespotioneering.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class TintedSplashParticle extends RainSplashParticle {
    private TintedSplashParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.gravityStrength = 0.04F;
        if (motionY == 0.0D && (motionX != 0.0D || motionZ != 0.0D)) {
            this.velocityX = motionX;
            this.velocityY = 0.1D;
            this.velocityZ = motionZ;
        }

    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TintedSplashParticle splashparticle = new TintedSplashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            splashparticle.setSprite(this.spriteSet);
            return splashparticle;
        }
    }
}