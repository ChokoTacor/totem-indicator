package com.totemindicator.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Нужен, чтобы узнать, где на экране начинается окно инвентаря. */
@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x")
    int totemindicator$getX();

    @Accessor("y")
    int totemindicator$getY();
}
