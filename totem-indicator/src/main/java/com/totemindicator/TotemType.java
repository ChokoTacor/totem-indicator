package com.totemindicator;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.Locale;

public enum TotemType {
    //            название     цвет (ARGB)  ключевые слова в названии/описании предмета (маленькими буквами)
    INFINITY("Infinity", 0xFF3CFF3C, "infinity", "инфинити", "инфини"),   // зелёный
    STINGER("Stinger",   0xFFFF3C3C, "stinger", "стингер"),               // красный
    ETERNITY("Eternity", 0xFFB44CFF, "eternity", "этерни");               // фиолетовый (этернити / этерните)

    /** true = учитывать только тотемы бессмертия. Если на сервере кастомные тотемы сделаны из другого предмета - поставьте false. */
    private static final boolean ONLY_TOTEM_ITEM = true;

    // Маленькие заглавные буквы (ɪɴꜰɪɴɪᴛʏ и т.п.), которые любят сервера, -> обычные латинские
    private static final String SMALL_CAPS =
            "\u1D00\u0299\u1D04\u1D05\u1D07\uA730\u0262\u029C\u026A\u1D0A\u1D0B\u029F\u1D0D"
          + "\u0274\u1D0F\u1D18\u01EB\u0280\uA731\u1D1B\u1D1C\u1D20\u1D21x\u028F\u1D22";
    private static final String LATIN = "abcdefghijklmnopqrstuvwxyz";

    public final String displayName;
    public final int color;
    private final String[] keywords;

    TotemType(String displayName, int color, String... keywords) {
        this.displayName = displayName;
        this.color = color;
        this.keywords = keywords;
    }

    /** Определяет тип тотема по предмету или возвращает null. */
    public static TotemType of(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (ONLY_TOTEM_ITEM && !stack.isOf(Items.TOTEM_OF_UNDYING)) return null;

        // 1) сначала по названию
        TotemType byName = match(normalize(stack.getName().getString()));
        if (byName != null) return byName;

        // 2) потом по описанию (lore)
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore == null) return null;
        StringBuilder sb = new StringBuilder();
        for (Text line : lore.lines()) {
            sb.append(line.getString()).append(' ');
        }
        return match(normalize(sb.toString()));
    }

    private static TotemType match(String text) {
        for (TotemType type : values()) {
            for (String keyword : type.keywords) {
                if (text.contains(keyword)) return type;
            }
        }
        return null;
    }

    private static String normalize(String s) {
        String lower = s.toLowerCase(Locale.ROOT).replace('ё', 'е');
        StringBuilder out = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            int idx = SMALL_CAPS.indexOf(c);
            out.append(idx >= 0 ? LATIN.charAt(idx) : c);
        }
        return out.toString();
    }

    /** Толщина обводки в пикселях GUI. */
    private static final int OUTLINE_THICKNESS = 1;

    /** Цветная обводка слота 16x16 (без заливки). */
    public static void highlight(DrawContext ctx, int x, int y, int color) {
        int t = OUTLINE_THICKNESS;
        int x1 = x - 1, y1 = y - 1, x2 = x + 17, y2 = y + 17;
        ctx.fill(x1, y1, x2, y1 + t, color);          // верх
        ctx.fill(x1, y2 - t, x2, y2, color);          // низ
        ctx.fill(x1, y1 + t, x1 + t, y2 - t, color);  // лево
        ctx.fill(x2 - t, y1 + t, x2, y2 - t, color);  // право
    }

    /** Цветная метка рядом с ником игрока, у которого тотем в руке. */
    public Text tag() {
        int rgb = color & 0x00FFFFFF;
        return Text.literal(" [" + displayName + "]").styled(style -> style.withColor(rgb));
    }
}
