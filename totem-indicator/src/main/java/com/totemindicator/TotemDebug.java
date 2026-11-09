package com.totemindicator;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

/** Команда /totemdebug: показывает, как называется предмет в руках и распознан ли он как тотем. */
public final class TotemDebug {
    private TotemDebug() {}

    public static void run(FabricClientCommandSource src) {
        ClientPlayerEntity player = src.getPlayer();
        dump(src, "Main hand", player.getMainHandStack());
        dump(src, "Off hand", player.getOffHandStack());
    }

    private static void dump(FabricClientCommandSource src, String label, ItemStack stack) {
        if (stack.isEmpty()) {
            src.sendFeedback(Text.literal(label + ": empty"));
            return;
        }
        src.sendFeedback(Text.literal(label + ": " + Registries.ITEM.getId(stack.getItem())
                + " | name = '" + stack.getName().getString() + "'"));

        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                src.sendFeedback(Text.literal("   lore: " + line.getString()));
            }
        }
        TotemType type = TotemType.of(stack);
        src.sendFeedback(Text.literal("   -> " + (type == null ? "NOT recognized" : type.displayName)));
    }
}
