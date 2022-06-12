package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class PotionInjectorBlock extends Block implements BlockEntityProvider {
    public PotionInjectorBlock(Settings properties) {
        super(properties);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HAS_GAUNTLET,false));
    }

    public static final String TRANS_KEY = "davespotioneering.container.potion_injector";

    public static final Text CONTAINER_NAME = new TranslatableText(TRANS_KEY);

    public static final BooleanProperty HAS_GAUNTLET = BooleanProperty.of("has_gauntlet");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
        if (worldIn.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen((NamedScreenHandlerFactory) worldIn.getBlockEntity(pos));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResult.CONSUME;
        }
    }

    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);

    public static final VoxelShape[] SHAPES =
            new VoxelShape[]{VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,EAST_SHAPE, BooleanBiFunction.OR),
                    VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,SOUTH_SHAPE, BooleanBiFunction.OR),
                    VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,WEST_SHAPE, BooleanBiFunction.OR),
                    VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,NORTH_SHAPE, BooleanBiFunction.OR),
            };

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).getHorizontal()];
    }

    /*@Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1;
    }*/

    public static void setHasGauntlet(World worldIn, BlockPos pos, BlockState state, boolean hasBook) {
        worldIn.setBlockState(pos, state.with(HAS_GAUNTLET, hasBook), 3);
        //notifyNeighbors(worldIn, pos, state);
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HAS_GAUNTLET,FACING);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView worldIn, List<Text> tooltip, TooltipContext flagIn) {

        tooltip.add(new TranslatableText(getTranslationKey()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().formatted(Formatting.GRAY));

        tooltip.add(new TranslatableText(getTranslationKey()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().formatted(Formatting.GRAY));
    }

    public MutableText getShiftDescription() {
        return new TranslatableText(this.getTranslationKey() + ".shift.desc");
    }

    public MutableText getCtrlDescription() {
        return new TranslatableText(this.getTranslationKey() + ".ctrl.desc");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos,BlockState state) {
        return new PotionInjectorBlockEntity(pos,state);
    }
}
