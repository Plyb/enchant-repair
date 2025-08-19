package io.github.orlouge.enchantrepair.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(method = "isEnchantable", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent;isEmpty()Z"))
    public boolean cursedItemsAreEnchantable(ItemEnchantmentsComponent iec) {
        return !iec.getEnchantments().stream().anyMatch(ench ->
                !ench.isIn(EnchantmentTags.CURSE) || ench.getKey().get().equals(Enchantments.VANISHING_CURSE)
        );
    }
}
