package com.totemindicator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;

/** Подсветка тотемов в хотбаре + список с количеством в левом верхнем углу. */
public final class TotemHud {
    private static final int LIST_X = 6;
    private static final int LIST_Y = 6;

    private TotemHud() {}

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.options.hudHidden) return;

        PlayerInventory inv = player.getInventory();
        int centerX = ctx.getScaledWindowWidth() / 2;
        int itemY = ctx.getScaledWindowHeight() - 16 - 3;

        // хотбар
        for (int i = 0; i < 9; i++) {
            TotemType type = TotemType.of(inv.getStack(i));
            if (type != null) {
                TotemType.highlight(ctx, centerX - 90 + i * 20 + 2, itemY, type.color);
            }
        }

        // слот второй руки
        TotemType offType = TotemType.of(player.getOffHandStack());
        if (offType != null) {
            int offX = player.getMainArm().getOpposite() == Arm.LEFT
                    ? centerX - 91 - 26
                    : centerX + 91 + 10;
            TotemType.highlight(ctx, offX, itemY, offType.color);
        }

        // список с количеством
        int[] counts = new int[TotemType.values().length];
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            TotemType type = TotemType.of(stack);
            if (type != null) counts[type.ordinal()] += stack.getCount();
        }

        int y = LIST_Y;
        for (TotemType type : TotemType.values()) {
            int n = counts[type.ordinal()];
            if (n == 0) continue;
            String line = type.displayName + " x" + n + (type == offType ? "  (offhand)" : "");
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line), LIST_X, y, type.color);
            y += 10;
        }
    }
}
