package io.github.orlouge.enchantrepair.mixin;

import io.github.orlouge.enchantrepair.Config;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net/minecraft/village/TradeOffers$EnchantBookFactory")
public class EnchantBookFactoryMixin {
    @ModifyVariable(method = "create", at = @At(value = "STORE", ordinal = 0))
    public ItemStack addCurseOfVanishing(ItemStack stack, Entity entity, Random random) {
        if (Config.CURSE_TRADED_BOOKS) {
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
                    stack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS)
            );
            var vanishing = entity.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.VANISHING_CURSE);
            builder.add(vanishing, 1);
            EnchantmentHelper.set(stack, builder.build());
        }
        return stack;
    }
}
