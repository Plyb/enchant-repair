package io.github.orlouge.enchantrepair.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import static net.minecraft.item.Items.register;

@Mixin(Items.class)
public class BookItemMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0), slice = @Slice(from = @At(value = "FIELD",
            target = "Lnet/minecraft/item/Items;PAPER:Lnet/minecraft/item/Item;")))
    private static Item noBookEnchant(String id, Item.Settings settings) {
        return register("book");

    }
}
