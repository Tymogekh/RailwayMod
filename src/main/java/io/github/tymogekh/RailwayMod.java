package io.github.tymogekh;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RailwayMod.MOD_ID)
public class RailwayMod {
    public static final String MOD_ID = "railwaymod";

    public RailwayMod(IEventBus bus) {
        Init.BLOCKS.register(bus);
        Init.ITEMS.register(bus);
        Init.CREATIVE_TABS.register(bus);
    }
}
