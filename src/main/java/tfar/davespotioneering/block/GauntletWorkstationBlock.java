package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import tfar.davespotioneering.menu.GauntletMenu;

import javax.annotation.Nullable;

public class GauntletWorkstationBlock extends Block {
    public GauntletWorkstationBlock(Properties properties) {
        super(properties);
    }
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("davespotioneering.container.gauntlet_workbench");


    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(
                    new SimpleNamedContainerProvider((id, inventory, player1) -> new GauntletMenu(id, inventory, loadFromGauntlet(player1)), CONTAINER_NAME));
            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResultType.CONSUME;
        }
    }

    public static ItemStackHandler loadFromGauntlet(PlayerEntity player) {
        ItemStackHandler handler =  new ItemStackHandler(7);
        return handler;
    }
}
