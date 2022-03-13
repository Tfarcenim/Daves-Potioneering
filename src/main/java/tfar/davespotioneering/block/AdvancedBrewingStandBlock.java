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

public class AdvancedBrewingStandBlock extends BrewingStandBlock {
    public AdvancedBrewingStandBlock(Properties properties) {
        super(properties);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof AdvancedBrewingStandBlockEntity) {
                player.openContainer((AdvancedBrewingStandBlockEntity)tileentity);
                player.addStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().mergeStyle(TextFormatting.GRAY));

        tooltip.add(new TranslationTextComponent(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().mergeStyle(TextFormatting.GRAY));
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".shift.desc");
    }

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".ctrl.desc");
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new AdvancedBrewingStandBlockEntity();
    }
}
