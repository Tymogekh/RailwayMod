package io.github.tymogekh.block;

import com.mojang.serialization.MapCodec;
import io.github.tymogekh.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class RailCurveBlockX2 extends BaseRailBlock {
    public static final MapCodec<RailCurveBlockX2> CODEC = simpleCodec(RailCurveBlockX2::new);
    public static final EnumProperty<@NotNull CurvePart> PART = EnumProperty.create("part", CurvePart.class);

    public RailCurveBlockX2(Properties properties) {
        super(false, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(RailBlock.SHAPE, RailShape.SOUTH_EAST).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH).setValue(RailBlock.WATERLOGGED, false).setValue(PART, CurvePart.MAIN));
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
    public boolean isValidRailShape(@NotNull RailShape shape) {
        return false;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        Direction direction = context.getHorizontalDirection();
        state = state.setValue(HorizontalDirectionalBlock.FACING, direction).setValue(RailBlock.SHAPE, getShapeFromDirection(direction));
        boolean isWaterlogged = context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER);
        return state.setValue(RailBlock.WATERLOGGED, isWaterlogged);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RailBlock.SHAPE, HorizontalDirectionalBlock.FACING, RailBlock.WATERLOGGED, PART);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
        return switch (state.getValue(PART)) {
            case MAIN -> hasMatchingBlocksAround(state, level, pos, direction);
            case UPPER -> hasMatchingBlocksAround(state, level, pos, direction.getOpposite());
            case INNER_BIT -> hasMatchingBlocksAround(state, level, pos, direction.getCounterClockWise());
            case OUTER_BIT -> hasMatchingBlocksAround(state, level, pos, direction.getClockWise());
        };
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull LevelReader level, @NotNull ScheduledTickAccess ticks, @NotNull BlockPos pos, @NotNull Direction directionToNeighbour, @NotNull BlockPos neighbourPos, @NotNull BlockState neighbourState, @NotNull RandomSource random) {
        return state.canSurvive(level, pos) ? super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random) : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && state.getValue(PART) == CurvePart.MAIN) {
            Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
            RailShape shape = getShapeFromDirection(direction);
            level.setBlock(pos.relative(direction, 1), Init.RAIL_CURVE_X2_BLOCK.get().defaultBlockState().setValue(RailBlock.SHAPE, shape).setValue(PART, CurvePart.OUTER_BIT).setValue(HorizontalDirectionalBlock.FACING, direction), Block.UPDATE_SKIP_ALL_SIDEEFFECTS | Block.UPDATE_CLIENTS);
            level.setBlock(pos.relative(direction.getClockWise(), 1), Init.RAIL_CURVE_X2_BLOCK.get().defaultBlockState().setValue(RailBlock.SHAPE, shape).setValue(PART, CurvePart.INNER_BIT).setValue(HorizontalDirectionalBlock.FACING, direction), Block.UPDATE_SKIP_ALL_SIDEEFFECTS | Block.UPDATE_CLIENTS);
            level.setBlock(pos.relative(direction, 1).relative(direction.getClockWise(), 1), Init.RAIL_CURVE_X2_BLOCK.get().defaultBlockState().setValue(RailBlock.SHAPE, getStraightShapeFromDirection(direction.getClockWise())).setValue(PART, CurvePart.UPPER).setValue(HorizontalDirectionalBlock.FACING, direction), Block.UPDATE_SKIP_ALL_SIDEEFFECTS | Block.UPDATE_CLIENTS);
        }
    }

    public boolean hasMatchingBlocksAround(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
        if (!level.isClientSide()) {
            BlockPos samplePos = pos;
            for (int i = 0; i < 3; i++) {
                if (i == 2) {
                    samplePos = samplePos.relative(direction.getClockWise(), 1);
                }
                BlockState sampleState = level.getBlockState(samplePos.relative(direction, i % 2));
                if (!sampleState.is(Init.RAIL_CURVE_X2_BLOCK.get())) {
                    return false;
                }
            }
        }
        return super.canSurvive(state, level, pos);
    }

    private static RailShape getShapeFromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> RailShape.SOUTH_EAST;
            case SOUTH -> RailShape.NORTH_WEST;
            case WEST -> RailShape.NORTH_EAST;
            case EAST -> RailShape.SOUTH_WEST;
            default -> throw new IllegalArgumentException("Rail curves can't be vertical!");
        };
    }

    public static RailShape getStraightShapeFromDirection(Direction direction) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return RailShape.NORTH_SOUTH;
        }
        if (direction == Direction.WEST || direction == Direction.EAST) {
            return RailShape.EAST_WEST;
        }
        throw new IllegalArgumentException("Rail curves can't be vertical!");
    }

    public static boolean canPlaceCurve(Level level, BlockPos pos, Direction placeDirection) {
        for (int i = 0; i < 3; i++) {
            if (i == 2) {
                pos = pos.relative(placeDirection.getClockWise(), 1);
            }
            BlockState state = level.getBlockState(pos.relative(placeDirection, i % 2));
            if (!state.is(Blocks.AIR)) {
                return false;
            }
        }
        return true;
    }

    public enum CurvePart implements StringRepresentable {
        MAIN("main"),
        OUTER_BIT("outer_bit"),
        INNER_BIT("inner_bit"),
        UPPER("upper");

        private final String name;

        CurvePart(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
