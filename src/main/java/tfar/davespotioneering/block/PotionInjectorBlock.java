package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import tfar.davespotioneering.blockentity.PotionInjectorBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class PotionInjectorBlock extends Block {
    public PotionInjectorBlock(Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HAS_GAUNTLET,false));
    }

    public static final String TRANS_KEY = "davespotioneering.container.potion_injector";

    public static final ITextComponent CONTAINER_NAME = new TranslationTextComponent(TRANS_KEY);

    public static final BooleanProperty HAS_GAUNTLET = BooleanProperty.create("has_gauntlet");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer((INamedContainerProvider) worldIn.getTileEntity(pos));
            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResultType.CONSUME;
        }
    }

    protected static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    protected static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);

    public static final VoxelShape[] SHAPES =
            new VoxelShape[]{VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,EAST_SHAPE, IBooleanFunction.OR),
                    VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,SOUTH_SHAPE, IBooleanFunction.OR),
                    VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,WEST_SHAPE, IBooleanFunction.OR),
                    VoxelShapes.combineAndSimplify(BOTTOM_SHAPE,NORTH_SHAPE, IBooleanFunction.OR),
            };

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(FACING).getHorizontalIndex()];
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

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HAS_GAUNTLET,FACING);
    }

    public static ItemStackHandler loadFromGauntlet(PlayerEntity player) {
        ItemStackHandler handler =  new ItemStackHandler(7);
        return handler;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PotionInjectorBlockEntity();
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

    public IFormattableTextComponent getCtrlDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".ctrl.desc");
    }

    public IFormattableTextComponent getShiftDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".shift.desc");
    }

}
