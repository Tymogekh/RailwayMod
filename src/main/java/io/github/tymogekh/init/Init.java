package io.github.tymogekh.init;

import io.github.tymogekh.RailwayMod;
import io.github.tymogekh.block.*;
import io.github.tymogekh.item.RailWrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class Init {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RailwayMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RailwayMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, RailwayMod.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RailwayMod.MOD_ID);

    public static final DeferredBlock<@NotNull RailIntersectionBlock> RAIL_INTERSECTION_BLOCK = BLOCKS.registerBlock("rail_intersection", properties -> new RailIntersectionBlock(properties.noCollision().strength(0.7F).sound(SoundType.METAL)));
    public static final DeferredItem<@NotNull BlockItem> RAIL_INTERSECTION_ITEM = ITEMS.registerItem("rail_intersection", properties -> new BlockItem(RAIL_INTERSECTION_BLOCK.get(), properties));

    public static final DeferredBlock<@NotNull RailSplitIntersectionBlock> RAIL_SPLIT_INTERSECTION_BLOCK = BLOCKS.registerBlock("rail_split_intersection", properties -> new RailSplitIntersectionBlock(properties.noCollision().strength(0.7F).sound(SoundType.METAL)));
    public static final DeferredItem<@NotNull BlockItem> RAIL_SPLIT_INTERSECTION_ITEM = ITEMS.registerItem("rail_split_intersection", properties -> new BlockItem(RAIL_SPLIT_INTERSECTION_BLOCK.get(), properties));

    public static final DeferredBlock<@NotNull RailTIntersectionBlock> RAIL_T_INTERSECTION_BLOCK = BLOCKS.registerBlock("rail_t_intersection", properties -> new RailTIntersectionBlock(properties.noCollision().strength(0.7F).sound(SoundType.METAL)));
    public static final DeferredItem<@NotNull BlockItem> RAIL_T_INTERSECTION_ITEM = ITEMS.registerItem("rail_t_intersection", properties -> new BlockItem(RAIL_T_INTERSECTION_BLOCK.get(), properties));

    public static final DeferredBlock<@NotNull StopRailBlock> STOP_RAIL_BLOCK = BLOCKS.registerBlock("stop_rail", properties -> new StopRailBlock(properties.noCollision().strength(0.7F).sound(SoundType.METAL)));
    public static final DeferredItem<@NotNull BlockItem> STOP_RAIL_ITEM = ITEMS.registerItem("stop_rail", properties -> new BlockItem(STOP_RAIL_BLOCK.get(), properties));

    public static final DeferredBlock<@NotNull RailCurveBlockX2> RAIL_CURVE_X2_BLOCK = BLOCKS.registerBlock("rail_curve_2x", properties -> new RailCurveBlockX2(properties.noCollision().strength(0.7F).sound(SoundType.METAL)));

    public static final DeferredHolder<DataComponentType<?>, @NotNull DataComponentType<BlockPos>> SNAP_POSITION = DATA_COMPONENT_TYPES.register("snap_position", () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).build());

    public static final DeferredItem<@NotNull RailWrenchItem> RAIL_WRENCH = ITEMS.registerItem("rail_wrench", properties -> new RailWrenchItem(properties.stacksTo(1)));

    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, @NotNull CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + RailwayMod.MOD_ID + ".creative_tab"))
            .icon(() -> RAIL_INTERSECTION_ITEM.get().asItem().getDefaultInstance())
            .displayItems((itemDisplayParameters, output) -> {
                    output.accept(RAIL_INTERSECTION_ITEM.get());
                    output.accept(RAIL_SPLIT_INTERSECTION_ITEM.get());
                    output.accept(RAIL_T_INTERSECTION_ITEM.get());
                    output.accept(STOP_RAIL_ITEM.get());
                    output.accept(RAIL_WRENCH.get());
            }).build());
}
