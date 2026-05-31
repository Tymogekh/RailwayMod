package io.github.tymogekh;

import io.github.tymogekh.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = RailwayMod.MOD_ID)
public class Events {

    @SubscribeEvent
    private static void gatherData(GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        event.addProvider(new ModelGen(packOutput));
        event.addProvider(new TagGen(packOutput, lookup));
        event.addProvider(new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(LootTableGen::new, LootContextParamSets.BLOCK)), lookup));
        event.addProvider(new LangGen(packOutput));
        event.addProvider(new RecipeGen.Runner(packOutput, lookup));
    }
}
