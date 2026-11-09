package com.totemindicator;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;

public class TotemIndicatorClient implements ClientModInitializer {
    public static final String MOD_ID = "totemindicator";

    @Override
    public void onInitializeClient() {
        // HUD: хотбар + список тотемов
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.of(MOD_ID, "totem_hud"),
                TotemHud::render);

        // Подсветка в инвентаре и контейнерах
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof HandledScreen<?>) {
                ScreenEvents.afterRender(screen).register(TotemScreenOverlay::render);
            }
        });

        // /totemdebug - клиентская команда, на сервер не отправляется
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("totemdebug").executes(context -> {
                    TotemDebug.run(context.getSource());
                    return Command.SINGLE_SUCCESS;
                })));
    }
}
