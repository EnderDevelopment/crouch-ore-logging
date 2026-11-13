package com.enderdevelopment.crouchbreak;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public final class CrouchBreakMod implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerBlockBreakEvents.BEFORE.register(ControlledBreaking::beforeBreak);
    }
}
