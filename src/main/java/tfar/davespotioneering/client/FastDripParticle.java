package tfar.davespotioneering.client;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FastDripParticle extends SpriteTexturedParticle {
    private final Fluid fluid;
    protected boolean fullbright;

    private FastDripParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z);
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.fluid = fluid;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getBrightnessForRender(float partialTick) {
        return this.fullbright ? 240 : super.getBrightnessForRender(partialTick);
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.ageParticle();
        if (!this.isExpired) {
            this.motionY -= this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMotion();
            if (!this.isExpired) {
                this.motionX *= 0.98F;
                this.motionY *= 0.98F;
                this.motionZ *= 0.98F;
                BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
                FluidState fluidstate = this.world.getFluidState(blockpos);
                if (fluidstate.getFluid() == this.fluid && this.posY < (double)((float)blockpos.getY() + fluidstate.getActualHeight(this.world, blockpos))) {
                    this.setExpired();
                }

            }
        }
    }

    protected void ageParticle() {
        if (this.maxAge-- <= 0) {
            this.setExpired();
        }

    }

    protected void updateMotion() {
    }

    @OnlyIn(Dist.CLIENT)
    public static class Dripping extends FastDripParticle {
        private final IParticleData particleData;

        public Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData particleData) {
            super(world, x, y, z, fluid);
            this.particleData = particleData;
            this.particleGravity *= 0.02F;
            this.maxAge = 5;
        }

        protected void ageParticle() {
            if (this.maxAge-- <= 0) {
                this.setExpired();
                this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
            }

        }

        protected void updateMotion() {
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class DrippingWaterFactory implements IParticleFactory<BasicParticleType> {
        protected final IAnimatedSprite spriteSet;

        public DrippingWaterFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FastDripParticle dripparticle = new FastDripParticle.Dripping(worldIn, x, y, z, Fluids.WATER, ParticleTypes.FALLING_WATER);
            dripparticle.setColor(0.2F, 0.3F, 1.0F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class FallingLiquidParticle extends FastDripParticle.FallingNectarParticle {
        protected final IParticleData particleData;

        private FallingLiquidParticle(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData particleData) {
            super(world, x, y, z, fluid);
            this.particleData = particleData;
        }

        protected void updateMotion() {
            if (this.onGround) {
                this.setExpired();
                this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }

        }
    }


    static class FallingNectarParticle extends FastDripParticle {
        private FallingNectarParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
            super(world, x, y, z, fluid);
            this.maxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
        }

        protected void updateMotion() {
            if (this.onGround) {
                this.setExpired();
            }
        }
    }
}
