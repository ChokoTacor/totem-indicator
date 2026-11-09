package com.totemindicator.mixin;

import com.totemindicator.TotemType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Если у другого игрока в руке тотем Infinity / Stinger / Eternity - добавляет к его нику
 * цветную метку. Показывается только когда игрок в прямой видимости (не сквозь стены).
 */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(
            method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At("RETURN")
    )
    private void totemindicator$addTotemTag(AbstractClientPlayerEntity player,
                                            PlayerEntityRenderState state,
                                            float tickDelta,
                                            CallbackInfo ci) {
        if (state.displayName == null) return;

        ClientPlayerEntity self = MinecraftClient.getInstance().player;
        if (self == null || player == self || !self.canSee(player)) return;

        TotemType main = TotemType.of(player.getMainHandStack());
        TotemType off = TotemType.of(player.getOffHandStack());
        if (main == null && off == null) return;

        MutableText text = Text.empty().append(state.displayName);
        if (main != null) text.append(main.tag());
        if (off != null && off != main) text.append(off.tag());
        state.displayName = text;
    }
}
