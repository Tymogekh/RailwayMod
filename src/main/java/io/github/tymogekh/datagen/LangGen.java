package io.github.tymogekh.datagen;

import io.github.tymogekh.Init;
import io.github.tymogekh.RailwayMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LangGen extends LanguageProvider {

    public LangGen(PackOutput output) {
        super(output, RailwayMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(Init.RAIL_INTERSECTION_BLOCK.get(), "Rail Intersection");
        add(Init.RAIL_INTERSECTION_ITEM.get(), "Rail Intersection");
        add(Init.RAIL_SPLIT_INTERSECTION_BLOCK.get(), "Rail Split Intersection");
        add(Init.RAIL_SPLIT_INTERSECTION_ITEM.get(), "Rail Split Intersection");
        add(Init.RAIL_T_INTERSECTION_BLOCK.get(), "Rail T Intersection");
        add(Init.RAIL_T_INTERSECTION_ITEM.get(), "Rail T Intersection");
        add("item_group." + RailwayMod.MOD_ID + ".creative_tab", "Railway Mod");
    }
}
