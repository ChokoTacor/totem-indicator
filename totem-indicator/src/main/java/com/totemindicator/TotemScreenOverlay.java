package com.totemindicator;

import com.totemindicator.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

/** Подсветка тотемов во всех окнах с инвентарём (инвентарь, сундук, шалкер и т.д.). */
public final class TotemScreenOverlay {
    private TotemScreenOverlay() {}

    public static void render(Screen screen, DrawContext ctx, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof HandledScreen<?> handled)) return;

        HandledScreenAccessor acc = (HandledScreenAccessor) handled;
        int left = acc.totemindicator$getX();
        int top = acc.totemindicator$getY();

        for (Slot slot : handled.getScreenHandler().slots) {
            if (!slot.isEnabled()) continue;
            TotemType type = TotemType.of(slot.getStack());
            if (type != null) {
                TotemType.highlight(ctx, left + slot.x, top + slot.y, type.color);
            }
        }
    }
}
