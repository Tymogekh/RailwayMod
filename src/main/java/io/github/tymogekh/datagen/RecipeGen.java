package io.github.tymogekh.datagen;

import io.github.tymogekh.Init;
import io.github.tymogekh.RailwayMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RecipeProvider {

    private final HolderLookup.Provider registries;
    private final RecipeOutput output;

    protected RecipeGen(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.registries = registries;
        this.output = output;
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TRANSPORTATION, Init.RAIL_INTERSECTION_ITEM.get(), 5)
                .define('R', Items.RAIL).pattern(" R ").pattern("RRR").pattern(" R ").unlockedBy("has_rail", has(Items.RAIL)).save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TRANSPORTATION, Init.RAIL_SPLIT_INTERSECTION_ITEM.get(), 3)
                .define('R', Items.RAIL).pattern(" R ").pattern("R R").unlockedBy("has_rail", has(Items.RAIL)).save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TRANSPORTATION, Init.RAIL_T_INTERSECTION_ITEM.get(), 4)
                .define('R', Items.RAIL).pattern(" R ").pattern("RRR").unlockedBy("has_rail", has(Items.RAIL)).save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new RecipeGen(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return RailwayMod.MOD_ID + "_recipe_provider";
        }
    }
}
