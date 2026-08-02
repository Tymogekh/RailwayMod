package io.github.tymogekh.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class RailTIntersectionBlock extends RailSplitIntersectionBlock{
    public static final MapCodec<RailTIntersectionBlock> CODEC = simpleCodec(RailTIntersectionBlock::new);

    public RailTIntersectionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RailShape getRailDirection(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @Nullable AbstractMinecart cart) {
        if (cart != null) {
            Direction cartDirection = cart.getDirection().getClockWise();
            Direction railDirection = state.getValue(HorizontalDirectionalBlock.FACING);
            if (cartDirection == railDirection.getClockWise() || cartDirection == railDirection.getCounterClockWise()) {
                if (cartDirection == Direction.NORTH || cartDirection == Direction.SOUTH) {
                    return RailShape.NORTH_SOUTH;
                } else if (cartDirection == Direction.WEST || cartDirection == Direction.EAST) {
                    return RailShape.EAST_WEST;
                }
            }
        }
        return super.getRailDirection(state, world, pos, cart);
    }
}
