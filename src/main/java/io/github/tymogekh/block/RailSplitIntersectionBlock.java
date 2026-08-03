package io.github.tymogekh.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class RailSplitIntersectionBlock extends BaseRailBlock {
    public static final MapCodec<RailSplitIntersectionBlock> CODEC = simpleCodec(RailSplitIntersectionBlock::new);
    public static final BooleanProperty POWERED = PoweredRailBlock.POWERED;
    public static final EnumProperty<@NotNull Direction> FACING = HorizontalDirectionalBlock.FACING;

    public RailSplitIntersectionBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(RailBlock.WATERLOGGED, false).setValue(RailBlock.SHAPE, RailShape.NORTH_EAST).setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull Property<@NotNull RailShape> getShapeProperty() {
        return RailBlock.SHAPE;
    }

    @Override
    protected @NotNull BlockState updateDir(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, boolean first) {
        return level.getBlockState(pos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        state = switch (context.getHorizontalDirection().get2DDataValue()) {
            case 0 -> state.setValue(RailBlock.SHAPE, RailShape.SOUTH_EAST).setValue(FACING, Direction.SOUTH);
            case 1 -> state.setValue(RailBlock.SHAPE, RailShape.SOUTH_WEST).setValue(FACING, Direction.WEST);
            case 2 -> state.setValue(RailBlock.SHAPE, RailShape.NORTH_WEST).setValue(FACING, Direction.NORTH);
            case 3 -> state.setValue(RailBlock.SHAPE, RailShape.NORTH_EAST).setValue(FACING, Direction.EAST);
            default -> state;
        };
        boolean isWaterlogged = context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER);
        return state.setValue(RailBlock.WATERLOGGED, isWaterlogged);
    }

    @Override
    public @NotNull RailShape getRailDirection(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @Nullable AbstractMinecart cart) {
        if (state.getValue(POWERED)) {
            return switch (state.getValue(RailBlock.SHAPE)) {
                case NORTH_WEST -> RailShape.NORTH_EAST;
                case NORTH_EAST -> RailShape.SOUTH_EAST;
                case SOUTH_EAST -> RailShape.SOUTH_WEST;
                default -> RailShape.NORTH_WEST;
            };
        }
        return state.getValue(RailBlock.SHAPE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RailBlock.SHAPE, RailBlock.WATERLOGGED, POWERED, FACING);
    }

    @Override
    protected void updateState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block) {
        boolean isPowered = state.getValue(POWERED);
        boolean shouldPower = level.hasNeighborSignal(pos);
        if (isPowered != shouldPower) {
            level.setBlock(pos, state.setValue(POWERED, shouldPower), Block.UPDATE_ALL);
        }
    }
}
