package tfar.davespotioneering.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AdvancedBrewingStandBlock extends BrewingStandBlock {
    public AdvancedBrewingStandBlock(Properties properties) {
        super(properties);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AdvancedBrewingStandBlockEntity) {
                player.openMenu((AdvancedBrewingStandBlockEntity)tileentity);
                player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }
            return InteractionResult.CONSUME;
        }
    }

    public static final int C_LINES = 3;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().withStyle(ChatFormatting.GRAY));

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(ChatFormatting.GRAY));
            }
    }

    public MutableComponent getShiftDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    public MutableComponent getCtrlDescriptions(int i) {
        return new TranslatableComponent(this.getDescriptionId() + i +".ctrl.desc");
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new AdvancedBrewingStandBlockEntity();
    }
}
