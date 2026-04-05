package dev.doctor4t.wathe.mixin.client.self;

import dev.doctor4t.wathe.client.gui.screen.ingame.NoteScreen;
import dev.doctor4t.wathe.item.NoteItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(NoteItem.class)
public class NoteItemMixin {
    // 在Client Mixin这里注入实现客户端服务端分离 但是这个注入在Loading阶段就会进行
    // 对于本地多人联机 这个实际上是双端的注入 因为最开始一定是Server/Render不分离的
    // Server线程需要一个额外的判定 不然setScreen是一定会GL报错的
    @Inject(method = "use", at = @At("HEAD"))
    private void useClient(@NotNull World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (world.isClient()){
            if (!user.isSneaking()) {
                return;
            }
            MinecraftClient.getInstance().setScreen(new NoteScreen());
        }
    }
}
