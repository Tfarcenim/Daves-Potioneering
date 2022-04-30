package tfar.davespotioneering.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.AbstractBlock.Properties;

public class AdvancedBrewingStandBlock extends BrewingStandBlock {
    public AdvancedBrewingStandBlock(Properties properties) {
        super(properties);
    }

    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AdvancedBrewingStandBlockEntity) {
                player.openMenu((AdvancedBrewingStandBlockEntity)tileentity);
                player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }
            return ActionResultType.CONSUME;
        }
    }

    public static final int C_LINES = 3;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().withStyle(TextFormatting.GRAY));

        tooltip.add(new TranslationTextComponent(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(TextFormatting.GRAY));
            }
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getDescriptionId() + ".shift.desc");
    }

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    public IFormattableTextComponent getCtrlDescriptions(int i) {
        return new TranslationTextComponent(this.getDescriptionId() + i +".ctrl.desc");
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new AdvancedBrewingStandBlockEntity();
    }
}
