package io.github.tymogekh.mixin;

import io.github.tymogekh.Init;
import io.github.tymogekh.block.StopRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(OldMinecartBehavior.class)
public abstract class MixinOldMinecartBehavior extends MinecartBehavior {
    protected MixinOldMinecartBehavior(AbstractMinecart minecart) {
        super(minecart);
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void checkForStopRail(CallbackInfo info, ServerLevel level, BlockPos var11, BlockState state) {
        if (state.is(Init.STOP_RAIL_BLOCK.get()) && !state.getValue(StopRailBlock.POWERED)) {
            this.minecart.setDeltaMovement(Vec3.ZERO);
        }
    }
}
