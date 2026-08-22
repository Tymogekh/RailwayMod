package io.github.tymogekh.block;

import com.mojang.serialization.MapCodec;
import io.github.tymogekh.accessor.LastMinecartPosAccessor;
import joptsimple.internal.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
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

import java.util.Locale;

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
    public boolean isValidRailShape(@NotNull RailShape shape) {
        return false;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        state = switch (context.getHorizontalDirection().getOpposite().get2DDataValue()) {
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
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RailBlock.SHAPE, RailBlock.WATERLOGGED, POWERED, FACING);
    }

    @Override
    public @NotNull RailShape getRailDirection(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @Nullable AbstractMinecart cart) {
        RailShape shape = state.getValue(RailBlock.SHAPE);
        Direction railDirection = state.getValue(FACING);
        boolean powered = state.getValue(POWERED);
        if (cart != null && isCollisionCourse(state, pos, cart, powered)) {
            return getSwitchedShape(shape, railDirection, powered);
        }
        return shape;
    }

    @Override
    protected void updateState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block) {
        boolean isPowered = state.getValue(POWERED);
        boolean shouldPower = level.hasNeighborSignal(pos);
        RailShape shape;
        if (isPowered != shouldPower) {
            if (shouldPower) {
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            shape = getSwitchedShape(state.getValue(RailBlock.SHAPE), state.getValue(FACING), isPowered);
            level.setBlock(pos, state.setValue(POWERED, shouldPower).setValue(RailBlock.SHAPE, shape), Block.UPDATE_ALL);
        }
    }

    protected boolean isCollisionCourse(BlockState state, BlockPos pos, AbstractMinecart cart, boolean powered) {
        MinecartBehavior behavior = cart.getBehavior();
        Direction target = state.getValue(HorizontalDirectionalBlock.FACING).getClockWise();
        if (behavior instanceof LastMinecartPosAccessor) {
            BlockPos lastCartPos = ((LastMinecartPosAccessor) behavior).railwaymod$getLastPos();
            if (powered) {
                target = target.getOpposite();
            }
            return lastCartPos.equals(pos.relative(target));
        }
        return false;
    }

    protected static RailShape getSwitchedShape(RailShape shape, Direction facing, boolean powered) {
        String[] exitNames = shape.getSerializedName().split("_");
        Direction target = facing.getClockWise();
        if (powered) {
            target = target.getOpposite();
        }
        if (target == Direction.NORTH || target == Direction.SOUTH) {
            exitNames[0] = target.getSerializedName();
        } else if (target == Direction.WEST || target == Direction.EAST) {
            exitNames[1] = target.getSerializedName();
        }
        return RailShape.valueOf(Strings.join(exitNames, "_").toUpperCase(Locale.ENGLISH));
    }
}
