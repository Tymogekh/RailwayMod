package io.github.tymogekh.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;

public class StopRailBlock extends BaseRailBlock {
    public static final MapCodec<StopRailBlock> CODEC = simpleCodec(StopRailBlock::new);
    public static final BooleanProperty POWERED = PoweredRailBlock.POWERED;

    public StopRailBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PoweredRailBlock.SHAPE, RailShape.NORTH_SOUTH).setValue(RailBlock.WATERLOGGED, false).setValue(POWERED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull Property<@NotNull RailShape> getShapeProperty() {
        return PoweredRailBlock.SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, @NotNull BlockState> builder) {
        builder.add(PoweredRailBlock.SHAPE, RailBlock.WATERLOGGED, POWERED);
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
