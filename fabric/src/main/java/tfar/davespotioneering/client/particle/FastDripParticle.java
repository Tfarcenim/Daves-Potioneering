package tfar.davespotioneering.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import tfar.davespotioneering.init.ModParticleTypes;
import tfar.davespotioneering.mixin.ParticleManagerAccess;

public class FastDripParticle extends TextureSheetParticle {
        private final Fluid fluid;
        protected boolean fullbright;

        private FastDripParticle(ClientLevel world, double x, double y, double z, Fluid fluid) {
            super(world, x, y, z);
            this.setSize(0.01F, 0.01F);
            this.gravity = 0.10F;
            this.fluid = fluid;
        }

        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        }

        public int getLightColor(float partialTick) {
            return this.fullbright ? 240 : super.getLightColor(partialTick);
        }

        public void tick() {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.ageParticle();
            if (!this.removed) {
                this.yd -= this.gravity;
                this.move(this.xd, this.yd, this.zd);
                this.updateMotion();
                if (!this.removed) {
                    this.xd *= 0.98F;
                    this.yd *= 0.98F;
                    this.zd *= 0.98F;
                    BlockPos blockpos = new BlockPos((int) this.x, (int) this.y, (int) this.z);
                    FluidState fluidstate = this.level.getFluidState(blockpos);
                    if (fluidstate.getType() == this.fluid && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
                        this.remove();
                    }

                }
            }
        }

        protected void ageParticle() {
            if (this.lifetime-- <= 0) {
                this.remove();
            }

        }

        protected void updateMotion() {
        }

        public static class Dripping extends FastDripParticle {
            private final ParticleOptions particleData;

            Dripping(ClientLevel world, double x, double y, double z, Fluid fluid, ParticleOptions particleData) {
                super(world, x, y, z, fluid);
                this.particleData = particleData;
                //this.particleGravity *= 0.02F;
                this.lifetime = 10;
            }

            protected void ageParticle() {
                if (this.lifetime-- <= 0) {
                    this.remove();


                    //turns into fast_falling_water
                    Particle particle = ((ParticleManagerAccess) Minecraft.getInstance().particleEngine).$makeParticle(this.particleData, this.x, this.y, this.z, this.xd, this.yd, this.zd);

                    particle.setColor(rCol,gCol,bCol);


                    Minecraft.getInstance().particleEngine.add(particle);

                    //this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
                }

            }

            protected void updateMotion() {
                this.xd *= 0.02D;
                this.yd *= 0.02D;
                this.zd *= 0.02D;
            }
        }


    public static class DrippingWaterFactory implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet spriteSet;

        public DrippingWaterFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FastDripParticle dripparticle = new Dripping(worldIn, x, y, z, Fluids.WATER, ModParticleTypes.FAST_FALLING_WATER);
            dripparticle.pickSprite(this.spriteSet);
            return dripparticle;
        }
    }

    static class FallingLiquidParticle extends FallingNectarParticle {
        protected final ParticleOptions particleData;

        private FallingLiquidParticle(ClientLevel world, double x, double y, double z, Fluid fluid, ParticleOptions particleData) {
            super(world, x, y, z, fluid);
            this.particleData = particleData;
        }

        protected void updateMotion() {
            if (this.onGround) {
                this.remove();

                //turns into splash
                Particle particle = ((ParticleManagerAccess) Minecraft.getInstance().particleEngine).$makeParticle(this.particleData, this.x, this.y, this.z, this.xd, this.yd, this.zd);

                    particle.setColor(rCol,gCol,bCol);


                Minecraft.getInstance().particleEngine.add(particle);

                //this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }

        }
    }

    static class FallingNectarParticle extends FastDripParticle {
        private FallingNectarParticle(ClientLevel world, double x, double y, double z, Fluid fluid) {
            super(world, x, y, z, fluid);
            this.lifetime = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
        }

        protected void updateMotion() {
            if (this.onGround) {
                this.remove();
            }

        }
    }

    public static class FallingWaterFactory implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet spriteSet;

        public FallingWaterFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FastDripParticle dripparticle = new FallingLiquidParticle(worldIn, x, y, z, Fluids.WATER, ModParticleTypes.TINTED_SPLASH);
            //dripparticle.setColor(0.2F, 0.3F, 1.0F);
            dripparticle.pickSprite(this.spriteSet);
            return dripparticle;
        }
    }

}
