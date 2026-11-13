package com.enderdevelopment.crouchbreak;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

public final class ControlledBreaking {
    private static final ThreadLocal<Boolean> AREA_BREAK = ThreadLocal.withInitial(() -> false);

    private ControlledBreaking() {
    }

    public static boolean beforeBreak(Level level, Player player, BlockPos origin, BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
        if (level.isClientSide() || !player.isCrouching() || AREA_BREAK.get()) {
            return true;
        }

        ItemStack held = player.getMainHandItem();
        Identifier itemId = BuiltInRegistries.ITEM.getKey(held.getItem());
        String path = itemId.getPath();
        boolean axe = path.endsWith("_axe");
        boolean pickaxe = path.endsWith("_pickaxe");
        boolean matchingBlock = axe && state.is(BlockTags.LOGS) || pickaxe && state.is(BlockTags.ORES);
        if (!matchingBlock) {
            return true;
        }

        AREA_BREAK.set(true);
        try {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos target = origin.offset(x, 0, z);
                    BlockState targetState = level.getBlockState(target);
                    if ((axe && targetState.is(BlockTags.LOGS) || pickaxe && targetState.is(BlockTags.ORES))
                            && player.hasCorrectToolForDrops(targetState)) {
                        level.destroyBlock(target, true, player);
                    }
                }
            }
        } finally {
            AREA_BREAK.set(false);
        }
        return false;
    }
}
