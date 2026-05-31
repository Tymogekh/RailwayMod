package io.github.tymogekh.datagen;

import io.github.tymogekh.Init;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return Init.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get()).toList();
    }
}
