package io.github.tymogekh;

import io.github.tymogekh.block.RailIntersectionBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class Init {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RailwayMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RailwayMod.MOD_ID);

    public static final DeferredBlock<@NotNull RailIntersectionBlock> RAIL_INTERSECTION_BLOCK = BLOCKS.registerBlock("rail_intersection", properties -> new RailIntersectionBlock(properties.noCollision().strength(0.7F).sound(SoundType.METAL)));
    public static final DeferredItem<@NotNull BlockItem> RAIL_INTERSECTION_ITEM = ITEMS.registerItem("rail_intersection", properties -> new BlockItem(RAIL_INTERSECTION_BLOCK.get(), properties));
}
