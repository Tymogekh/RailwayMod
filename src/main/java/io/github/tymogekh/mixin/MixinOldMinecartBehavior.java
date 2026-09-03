package io.github.tymogekh.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.tymogekh.accessor.LastMinecartPosAccessor;
import io.github.tymogekh.init.Init;
import io.github.tymogekh.block.StopRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OldMinecartBehavior.class)
public abstract class MixinOldMinecartBehavior extends MinecartBehavior implements LastMinecartPosAccessor {
    @Unique
    private boolean railwaymod$hasStopped = false;
    @Unique
    private BlockPos railwaymod$currentPos = this.minecart.getCurrentBlockPosOrRailBelow();
    @Unique
    private BlockPos railwaymod$lastPos = this.minecart.getCurrentBlockPosOrRailBelow();

    protected MixinOldMinecartBehavior(AbstractMinecart minecart) {
        super(minecart);
    }

    @WrapOperation(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/OldMinecartBehavior;level()Lnet/minecraft/world/level/Level;", ordinal = 0))
    private Level updateLastPos(OldMinecartBehavior instance, Operation<Level> original) {
        BlockPos pos = this.minecart.getCurrentBlockPosOrRailBelow();
        if (!this.railwaymod$currentPos.equals(pos)) {
            this.railwaymod$lastPos = this.railwaymod$currentPos;
            this.railwaymod$currentPos = pos;
        }
        return original.call(instance);
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
            this.railwaymod$hasStopped = false;
            original.call(instance, level);
        }
    }

    @Override
    public BlockPos railwaymod$getLastPos() {
        return this.railwaymod$lastPos;
    }
}
