package io.github.tymogekh.datagen;

import io.github.tymogekh.Init;
import io.github.tymogekh.block.RailCurveBlockX2;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LootTableGen extends BlockLootSubProvider {
    public LootTableGen(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(Init.RAIL_INTERSECTION_BLOCK.get());
        dropSelf(Init.RAIL_SPLIT_INTERSECTION_BLOCK.get());
        dropSelf(Init.RAIL_T_INTERSECTION_BLOCK.get());
        dropSelf(Init.STOP_RAIL_BLOCK.get());
        add(Init.RAIL_CURVE_X2_BLOCK.get(), createCurveDrops(Init.RAIL_CURVE_X2_BLOCK.get()));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return Init.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get()).toList();
    }

    private LootTable.Builder createCurveDrops(Block block) {
        return this.applyExplosionDecay(block, LootTable.lootTable())
                .withPool(LootPool.lootPool()
                        .when(new LootItemBlockStatePropertyCondition.Builder(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RailCurveBlockX2.PART, RailCurveBlockX2.CurvePart.MAIN))
                                .or(new LootItemBlockStatePropertyCondition.Builder((block))
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RailCurveBlockX2.PART, RailCurveBlockX2.CurvePart.UPPER))))
                        .add(LootItem.lootTableItem(Items.RAIL)));
    }
}
