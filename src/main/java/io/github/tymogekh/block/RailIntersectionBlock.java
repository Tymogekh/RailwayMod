package io.github.tymogekh.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class RailIntersectionBlock extends BaseRailBlock {
    public static final MapCodec<RailIntersectionBlock> CODEC = simpleCodec(RailIntersectionBlock::new);

    public RailIntersectionBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(RailBlock.WATERLOGGED, false).setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH));
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
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(RailBlock.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
    }

    @Override
    public @NotNull RailShape getRailDirection(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @Nullable AbstractMinecart cart) {
        if (cart != null) {
            if (cart.getDirection() == Direction.NORTH || cart.getDirection() == Direction.SOUTH) {
                return RailShape.EAST_WEST;
            } else if (cart.getDirection() == Direction.WEST || cart.getDirection() == Direction.EAST) {
                return RailShape.NORTH_SOUTH;
            }
        }
        return super.getRailDirection(state, world, pos, cart);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RailBlock.SHAPE, RailBlock.WATERLOGGED);
    }
}
