package io.github.tymogekh.datagen;

import io.github.tymogekh.Init;
import io.github.tymogekh.RailwayMod;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import org.jetbrains.annotations.NotNull;

public class ModelGen extends ModelProvider {

    public ModelGen(PackOutput output) {
        super(output, RailwayMod.MOD_ID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        createTrivialRail(blockModels, Init.RAIL_INTERSECTION_BLOCK.get());
        createRightLeftRail(blockModels, Init.RAIL_SPLIT_INTERSECTION_BLOCK.get());
        createRightLeftRail(blockModels, Init.RAIL_T_INTERSECTION_BLOCK.get());
    }

    private static void createTrivialRail(BlockModelGenerators blockModels, Block block) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(ModelTemplates.RAIL_FLAT.create(block, TextureMapping.rail(TextureMapping.getBlockTexture(block)), blockModels.modelOutput))));
        blockModels.registerSimpleFlatItemModel(block);
    }

    private static void createRightLeftRail(BlockModelGenerators blockModels, Block block) {
        final String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(PoweredRailBlock.POWERED).generate(powered ->
                    BlockModelGenerators.plainVariant(ModelTemplates.RAIL_FLAT.createWithSuffix(block, powered ? "_left" : "_right", TextureMapping.rail(new Material(Identifier.fromNamespaceAndPath(RailwayMod.MOD_ID, "block/" + blockName + (powered ? "_left" : "_right")))), blockModels.modelOutput)))).with(PropertyDispatch.modify(HorizontalDirectionalBlock.FACING)
                        .select(Direction.EAST, BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
        blockModels.registerSimpleFlatItemModel(block, "_right");
    }
}
