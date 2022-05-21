package tfar.davespotioneering.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import tfar.davespotioneering.init.ModParticleTypes;
import tfar.davespotioneering.mixin.ParticleManagerAccess;

public class FastDripParticle extends SpriteBillboardParticle {
        private final Fluid fluid;
        protected boolean fullbright;

        private FastDripParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
            super(world, x, y, z);
            this.setBoundingBoxSpacing(0.01F, 0.01F);
            this.gravityStrength = 0.10F;
            this.fluid = fluid;
        }

        public ParticleTextureSheet getType() {
            return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        }

        public int getColorMultiplier(float partialTick) {
            return this.fullbright ? 240 : super.getColorMultiplier(partialTick);
        }

        public void tick() {
            this.prevPosX = this.x;
            this.prevPosY = this.y;
            this.prevPosZ = this.z;
            this.ageParticle();
            if (!this.dead) {
                this.velocityY -= this.gravityStrength;
                this.move(this.velocityX, this.velocityY, this.velocityZ);
                this.updateMotion();
                if (!this.dead) {
                    this.velocityX *= 0.98F;
                    this.velocityY *= 0.98F;
                    this.velocityZ *= 0.98F;
                    BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
                    FluidState fluidstate = this.world.getFluidState(blockpos);
                    if (fluidstate.getFluid() == this.fluid && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.world, blockpos))) {
                        this.markDead();
                    }

                }
            }
        }

        protected void ageParticle() {
            if (this.maxAge-- <= 0) {
                this.markDead();
            }

        }

        protected void updateMotion() {
        }

        public static class Dripping extends FastDripParticle {
            private final ParticleEffect particleData;

            Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect particleData) {
                super(world, x, y, z, fluid);
                this.particleData = particleData;
                //this.particleGravity *= 0.02F;
                this.maxAge = 10;
            }

            protected void ageParticle() {
                if (this.maxAge-- <= 0) {
                    this.markDead();


                    //turns into fast_falling_water
                    Particle particle = ((ParticleManagerAccess) MinecraftClient.getInstance().particleManager).$makeParticle(this.particleData, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);

                    particle.setColor(colorRed,colorGreen,colorBlue);


                    MinecraftClient.getInstance().particleManager.addParticle(particle);

                    //this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
                }

            }

            protected void updateMotion() {
                this.velocityX *= 0.02D;
                this.velocityY *= 0.02D;
                this.velocityZ *= 0.02D;
            }
        }


    public static class DrippingWaterFactory implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteSet;

        public DrippingWaterFactory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FastDripParticle dripparticle = new Dripping(worldIn, x, y, z, Fluids.WATER, ModParticleTypes.FAST_FALLING_WATER);
            dripparticle.setSprite(this.spriteSet);
            return dripparticle;
        }
    }

    static class FallingLiquidParticle extends FastDripParticle.FallingNectarParticle {
        protected final ParticleEffect particleData;

        private FallingLiquidParticle(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect particleData) {
            super(world, x, y, z, fluid);
            this.particleData = particleData;
        }

        protected void updateMotion() {
            if (this.onGround) {
                this.markDead();

                //turns into splash
                Particle particle = ((ParticleManagerAccess) MinecraftClient.getInstance().particleManager).$makeParticle(this.particleData, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);

                    particle.setColor(colorRed,colorGreen,colorBlue);


                MinecraftClient.getInstance().particleManager.addParticle(particle);

                //this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
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
                this.markDead();
            }

        }
    }

    public static class FallingWaterFactory implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteSet;

        public FallingWaterFactory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FastDripParticle dripparticle = new FastDripParticle.FallingLiquidParticle(worldIn, x, y, z, Fluids.WATER, ModParticleTypes.TINTED_SPLASH);
            //dripparticle.setColor(0.2F, 0.3F, 1.0F);
            dripparticle.setSprite(this.spriteSet);
            return dripparticle;
        }
    }

}
