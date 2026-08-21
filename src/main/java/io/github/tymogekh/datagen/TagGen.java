package io.github.tymogekh.datagen;

import io.github.tymogekh.init.Init;
import io.github.tymogekh.RailwayMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TagGen extends BlockTagsProvider {

    public TagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, RailwayMod.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.RAILS)
                .add(Init.RAIL_INTERSECTION_BLOCK.get())
                .add(Init.RAIL_SPLIT_INTERSECTION_BLOCK.get())
                .add(Init.RAIL_T_INTERSECTION_BLOCK.get())
                .add(Init.STOP_RAIL_BLOCK.get())
                .add(Init.RAIL_CURVE_X2_BLOCK.get());
        tag(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)
                .add(Init.RAIL_INTERSECTION_BLOCK.get())
                .add(Init.RAIL_INTERSECTION_BLOCK.get())
                .add(Init.RAIL_T_INTERSECTION_BLOCK.get())
                .add(Init.STOP_RAIL_BLOCK.get())
                .add(Init.RAIL_CURVE_X2_BLOCK.get());
    }
}
