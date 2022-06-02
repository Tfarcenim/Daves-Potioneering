package tfar.davespotioneering.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class PotionInjectorBlock extends Block implements EntityBlock {
    public PotionInjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_GAUNTLET,false));
    }

    public static final String TRANS_KEY = "davespotioneering.container.potion_injector";

    public static final Component CONTAINER_NAME = new TranslatableComponent(TRANS_KEY);

    public static final BooleanProperty HAS_GAUNTLET = BooleanProperty.create("has_gauntlet");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu((MenuProvider) worldIn.getBlockEntity(pos));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return InteractionResult.CONSUME;
        }
    }

    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    protected static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);

    public static final VoxelShape[] SHAPES =
            new VoxelShape[]{Shapes.join(BOTTOM_SHAPE,EAST_SHAPE, BooleanOp.OR),
                    Shapes.join(BOTTOM_SHAPE,SOUTH_SHAPE, BooleanOp.OR),
                    Shapes.join(BOTTOM_SHAPE,WEST_SHAPE, BooleanOp.OR),
                    Shapes.join(BOTTOM_SHAPE,NORTH_SHAPE, BooleanOp.OR),
            };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
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

    public static void setHasGauntlet(Level worldIn, BlockPos pos, BlockState state, boolean hasBook) {
        worldIn.setBlock(pos, state.setValue(HAS_GAUNTLET, hasBook), 3);
        //notifyNeighbors(worldIn, pos, state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_GAUNTLET,FACING);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown())
            tooltip.add(this.getShiftDescription().withStyle(ChatFormatting.GRAY));

        tooltip.add(new TranslatableComponent(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            tooltip.add(this.getCtrlDescription().withStyle(ChatFormatting.GRAY));
    }

    public MutableComponent getShiftDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".ctrl.desc");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new PotionInjectorBlockEntity(p_153215_,p_153216_);
    }
}
