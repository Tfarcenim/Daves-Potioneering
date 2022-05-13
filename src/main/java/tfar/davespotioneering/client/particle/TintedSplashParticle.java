package tfar.davespotioneering.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class TintedSplashParticle extends WaterDropParticle {
    private TintedSplashParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.gravity = 0.04F;
        if (motionY == 0.0D && (motionX != 0.0D || motionZ != 0.0D)) {
            this.xd = motionX;
            this.yd = 0.1D;
            this.zd = motionZ;
        }

    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TintedSplashParticle splashparticle = new TintedSplashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            splashparticle.pickSprite(this.spriteSet);
            return splashparticle;
        }
    }
}