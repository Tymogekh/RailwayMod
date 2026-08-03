package io.github.tymogekh.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.tymogekh.Init;
import io.github.tymogekh.block.StopRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OldMinecartBehavior.class)
public abstract class MixinOldMinecartBehavior extends MinecartBehavior {
    @Unique
    private boolean railwaymod$hasStopped = false;

    protected MixinOldMinecartBehavior(AbstractMinecart minecart) {
        super(minecart);
    }

    @WrapOperation(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/OldMinecartBehavior;moveAlongTrack(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void checkForStopRail(OldMinecartBehavior instance, ServerLevel level, Operation<Void> original) {
        BlockPos pos = this.minecart.getCurrentBlockPosOrRailBelow();
        BlockState state = level.getBlockState(pos);
        if (state.is(Init.STOP_RAIL_BLOCK.get()) && state.getValue(StopRailBlock.POWERED)) {
            if (!this.railwaymod$hasStopped) {
                level.playSound(null, pos, SoundType.HONEY_BLOCK.getHitSound(), SoundSource.BLOCKS);
            }
            this.setDeltaMovement(Vec3.ZERO);
            this.railwaymod$hasStopped = true;
        } else {
            original.call(instance, level);
            this.railwaymod$hasStopped = false;
        }
    }
}
