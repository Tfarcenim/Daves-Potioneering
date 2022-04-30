package tfar.davespotioneering.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.RainParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TintedSplashParticle extends RainParticle {
    private TintedSplashParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.gravity = 0.04F;
        if (motionY == 0.0D && (motionX != 0.0D || motionZ != 0.0D)) {
            this.xd = motionX;
            this.yd = 0.1D;
            this.zd = motionZ;
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TintedSplashParticle splashparticle = new TintedSplashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            splashparticle.pickSprite(this.spriteSet);
            return splashparticle;
        }
    }
}