package io.github.tymogekh.datagen;

import io.github.tymogekh.Init;
import io.github.tymogekh.RailwayMod;
import io.github.tymogekh.block.RailCurveBlockX2;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModelGen extends ModelProvider {
    private static final ModelTemplate RAIL_RAISED_SW = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(RailwayMod.MOD_ID, "block/template_rail_raised_sw")), Optional.of("_sw"), TextureSlot.RAIL);
    private static final ModelTemplate RAIL_RAISED_NE = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(RailwayMod.MOD_ID, "block/template_rail_raised_ne")), Optional.of("_ne"), TextureSlot.RAIL);

    public ModelGen(PackOutput output) {
        super(output, RailwayMod.MOD_ID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        createTrivialRail(blockModels, Init.RAIL_INTERSECTION_BLOCK.get());
        createRightLeftRail(blockModels, Init.RAIL_SPLIT_INTERSECTION_BLOCK.get());
        createRightLeftRail(blockModels, Init.RAIL_T_INTERSECTION_BLOCK.get());
        createActiveRail(blockModels, Init.STOP_RAIL_BLOCK.get());
        createX2Curve(blockModels, Init.RAIL_CURVE_X2_BLOCK.get());
        itemModels.generateFlatItem(Init.RAIL_WRENCH.get(), ModelTemplates.FLAT_ITEM);
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

    public static void createActiveRail(BlockModelGenerators blockModels, Block block) {
        MultiVariant flat = BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "", ModelTemplates.RAIL_FLAT, TextureMapping::rail));
        MultiVariant risingNE = BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_raised", RAIL_RAISED_NE, TextureMapping::rail));
        MultiVariant risingSW = BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_raised", RAIL_RAISED_SW, TextureMapping::rail));
        MultiVariant flatOn = BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_on", ModelTemplates.RAIL_FLAT, TextureMapping::rail));
        MultiVariant risingNEOn = BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_on_raised", RAIL_RAISED_NE, TextureMapping::rail));
        MultiVariant risingSWOn = BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_on_raised", RAIL_RAISED_SW, TextureMapping::rail));
        blockModels.registerSimpleFlatItemModel(block);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((powered, railShape) -> {
            MultiVariant var10000;
            switch (railShape) {
                case NORTH_SOUTH -> var10000 = powered ? flatOn : flat;
                case EAST_WEST -> var10000 = (powered ? flatOn : flat).with(BlockModelGenerators.Y_ROT_90);
                case ASCENDING_EAST -> var10000 = (powered ? risingNEOn : risingNE).with(BlockModelGenerators.Y_ROT_90);
                case ASCENDING_WEST -> var10000 = (powered ? risingSWOn : risingSW).with(BlockModelGenerators.Y_ROT_90);
                case ASCENDING_NORTH -> var10000 = powered ? risingNEOn : risingNE;
                case ASCENDING_SOUTH -> var10000 = powered ? risingSWOn : risingSW;
                default -> throw new UnsupportedOperationException("Fix your generator!");
            }

            return var10000;
        })));
    }

    public static void createX2Curve(BlockModelGenerators blockModels, Block block) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(RailCurveBlockX2.PART)
                        .select(RailCurveBlockX2.CurvePart.MAIN, BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "", ModelTemplates.RAIL_FLAT, TextureMapping::rail)))
                        .select(RailCurveBlockX2.CurvePart.UPPER, BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_upper", ModelTemplates.RAIL_FLAT, TextureMapping::rail)))
                        .select(RailCurveBlockX2.CurvePart.OUTER_BIT, BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_bit_out", ModelTemplates.RAIL_FLAT, TextureMapping::rail)))
                        .select(RailCurveBlockX2.CurvePart.INNER_BIT, BlockModelGenerators.plainVariant(blockModels.createSuffixedVariant(block, "_bit_inn", ModelTemplates.RAIL_FLAT, TextureMapping::rail)))
                ).with(PropertyDispatch.modify(HorizontalDirectionalBlock.FACING)
                        .select(Direction.EAST, BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)
                )
        );
    }
}
