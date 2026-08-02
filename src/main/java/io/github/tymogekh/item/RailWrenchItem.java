package io.github.tymogekh.item;

import io.github.tymogekh.Init;
import io.github.tymogekh.block.RailCurveBlockX2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RailWrenchItem extends Item {

    public RailWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack stack = player.getItemInHand(context.getHand());
            BlockPos clickedPos = context.getClickedPos();
            BlockPos savedPos = stack.get(Init.SNAP_POSITION.get());
            Level level = context.getLevel();
            if (player.getInventory().contains(itemStack -> itemStack.is(Items.RAIL) && itemStack.count() >= 2) && player.isShiftKeyDown()) {
                if (savedPos != null) {
                    if (clickedPos.equals(savedPos.north().east())) {
                        if (RailCurveBlockX2.canPlaceCurve(level, savedPos.above(), Direction.NORTH) && level.getBlockState(savedPos).isFaceSturdy(level, savedPos, Direction.UP)) {
                            placeCurve(level, savedPos, stack, player, Direction.NORTH);
                            return InteractionResult.SUCCESS;
                        }
                    } else if (clickedPos.equals(savedPos.north().west())) {
                        if (RailCurveBlockX2.canPlaceCurve(level, savedPos.above(), Direction.WEST) && level.getBlockState(savedPos).isFaceSturdy(level, savedPos, Direction.UP)) {
                            placeCurve(level, savedPos, stack, player, Direction.WEST);
                            return InteractionResult.SUCCESS;
                        }
                    } else if (clickedPos.equals(savedPos.south().west())) {
                        if (RailCurveBlockX2.canPlaceCurve(level, savedPos.above(), Direction.SOUTH) && level.getBlockState(savedPos).isFaceSturdy(level, savedPos, Direction.UP)) {
                            placeCurve(level, savedPos, stack, player, Direction.SOUTH);
                            return InteractionResult.SUCCESS;
                        }
                    } else if (clickedPos.equals(savedPos.south().east())) {
                        if (RailCurveBlockX2.canPlaceCurve(level, savedPos.above(), Direction.EAST) && level.getBlockState(savedPos).isFaceSturdy(level, savedPos, Direction.UP)) {
                            placeCurve(level, savedPos, stack, player, Direction.EAST);
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else {
                    stack.set(Init.SNAP_POSITION.get(), clickedPos);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        stack.set(Init.SNAP_POSITION.get(), null);
        return InteractionResult.SUCCESS;
    }

    private void placeCurve(Level level, BlockPos pos, ItemStack stack, Player player, Direction direction) {
        RailShape shape = RailCurveBlockX2.getStraightShapeFromDirection(direction);
        level.setBlock(pos.above(), Init.RAIL_CURVE_X2_BLOCK.get().defaultBlockState().setValue(RailCurveBlockX2.PART, RailCurveBlockX2.CurvePart.MAIN).setValue(HorizontalDirectionalBlock.FACING, direction).setValue(RailBlock.SHAPE, shape), HorizontalDirectionalBlock.UPDATE_KNOWN_SHAPE | HorizontalDirectionalBlock.UPDATE_CLIENTS);
        if (!player.isCreative()) {
            getMatchingStack(player.getInventory(), itemStack -> itemStack.is(Items.RAIL) && itemStack.count() >= 2).shrink(2);
        }
        stack.set(Init.SNAP_POSITION.get(), null);
    }

    private static ItemStack getMatchingStack(Inventory inventory, Predicate<ItemStack> predicate) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (predicate.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
