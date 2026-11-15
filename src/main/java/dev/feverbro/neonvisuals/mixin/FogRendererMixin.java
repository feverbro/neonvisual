package dev.feverbro.neonvisuals.mixin;

import dev.feverbro.neonvisuals.NeonConfig;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/** Increases only render-distance fog; it does not override water, lava or status-effect fog. */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @ModifyArgs(
        method="setupFog",
        at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"),
        require=0
    )
    private void neonvisuals$extendedFog(Args args) {
        if (NeonConfig.extendedFog) args.set(6,Math.max((float)args.get(6),4096.0F));
    }
}
