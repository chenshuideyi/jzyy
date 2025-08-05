package com.csdy.jzyy.mixins.void_walk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.hasVoidWalk;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    /**
     * @Inject 会在方法的开头注入代码。
     * 我们的目标是 getCollisionShape 方法，这是所有方块获取物理边界的最终源头。
     * cancellable = true 允许我们取消原方法的执行，并自己设定一个返回值。
     */
    @Inject(
            method = "getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void voidwalk_getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        // CollisionContext 中包含了正在尝试与方块交互的实体
        // 这是一个至关重要的信息来源！
        if (pContext instanceof EntityCollisionContext) {
            // 2. 将 pContext 强制转换为我们的 Accessor 接口。
            //    这个操作在运行时绝对安全。
            EntityCollisionContextAccessor accessor = (EntityCollisionContextAccessor) pContext;

            // 3. 使用我们定义的 getter 方法安全地获取实体。
            Entity entity = accessor.getEntity();

            // 4. 执行我们原来的逻辑。
            if (entity instanceof Player player && hasVoidWalk(player)) {
                // 对于这个玩家，所有方块的碰撞箱都视为空。
                cir.setReturnValue(Shapes.empty());
            }
        }
    }
}
